import cv2, time, speech_handler
import mediapipe as mp
import numpy as np
from pydub import AudioSegment
from pydub.playback import play
import simpleaudio

mp_drawing = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_styles
mp_face_mesh = mp.solutions.face_mesh
eye_closed_start_time = None
has_queried = False
EAR_THRESHOLD = 0.25#0.15
EXTENDED_BLINK_DURATION = 0.5#.3
avg_blinks_per_10_second = []
last_blink = 0
WINDOW_SIZE = 150  # for example, if processing at 5 fps for a 30-second window

HEAD_LANDMARK_IDS = [1, 152, 33, 263, 61, 291]
HEAD_DROOP_THRESHOLD = -160  # Pitch in degrees above which head is considered drooping
HEAD_DROOP_DURATION = 2
MODEL_POINTS = np.array([
    [0.0, 0.0, 0.0],       # Nose tip
    [0.0, -63.6, -12.5],   # Chin
    [-43.3, 32.7, -26.0],  # Left eye left corner
    [43.3, 32.7, -26.0],   # Right eye right corner
    [-28.9, -28.9, -24.1], # Left mouth corner (approximate)
    [28.9, -28.9, -24.1]   # Right mouth corner (approximate)
], dtype=np.float32)

last_head_droop = None


drawing_spec = mp_drawing.DrawingSpec(thickness=1, circle_radius=1)
cap = cv2.VideoCapture(0)
new_speech_handler = speech_handler.main()

RIGHT_EYE = [33, 160, 158, 133, 153, 144]
LEFT_EYE  = [263, 387, 385, 362, 380, 373]

def compute_EAR(landmarks, eye_indices):
    eye = np.array([[landmarks[i].x, landmarks[i].y] for i in eye_indices]) # Extract the coordinates for the eye landmarks
    vertical1 = np.linalg.norm(eye[1] - eye[5]) # Calculate Euclidean distances between the vertical eye landmarks
    vertical2 = np.linalg.norm(eye[2] - eye[4])
    horizontal = np.linalg.norm(eye[0] - eye[3]) # Horizontal distance

    return (vertical1 + vertical2) / (2.0 * horizontal)

def compute_avg_EAR(face_landmarks):
    right_EAR = compute_EAR(face_landmarks.landmark,RIGHT_EYE)
    left_EAR = compute_EAR(face_landmarks.landmark,LEFT_EYE)
    #print((right_EAR + left_EAR)/2)
    return (right_EAR + left_EAR)/2

def compute_avg_blinks():
    # For every new frame:
    if compute_EAR(eye_landmarks) < EAR_THRESHOLD:
        window_frames.append(1)  # 1 indicates eyes closed
    else:
        window_frames.append(0)  # 0 indicates eyes open

    # Keep the sliding window size constant
    if len(window_frames) > WINDOW_SIZE:
        window_frames.pop(0)

    if len(window_frames) == WINDOW_SIZE:
        perclos = sum(window_frames) / WINDOW_SIZE
        if perclos > PERCLOS_THRESHOLD:
            trigger_perclos_alert()

def get_pupil_center(landmarks, iris_indices):
    """
    Calculate the pupil center by averaging the normalized coordinates
    of the iris landmarks.
    """
    iris_points = np.array([[landmarks[i].x, landmarks[i].y] for i in iris_indices])
    center = np.mean(iris_points, axis=0)
    return center

def get_head_pose(landmarks, image_shape):
    """
    Estimate head pose by converting selected facial landmarks to 2D image points,
    and then using cv2.solvePnP with a predefined 3D model.
    Returns Euler angles (pitch, yaw, roll) in degrees.
    (New Addition)
    """
    height, width, _ = image_shape
    image_points = []
    for idx in HEAD_LANDMARK_IDS:
        lm = landmarks[idx]
        x = lm.x * width
        y = lm.y * height
        image_points.append([x, y])
    image_points = np.array(image_points, dtype=np.float32)

    # Define the camera matrix using the image dimensions.
    focal_length = width
    center = (width / 2, height / 2)
    camera_matrix = np.array([
        [focal_length, 0, center[0]],
        [0, focal_length, center[1]],
        [0, 0, 1]
    ], dtype=np.float32)
    dist_coeffs = np.zeros((4, 1))  # Assuming no lens distortion

    success, rotation_vector, translation_vector = cv2.solvePnP(
        MODEL_POINTS, image_points, camera_matrix, dist_coeffs, flags=cv2.SOLVEPNP_ITERATIVE)

    if not success:
        return None, None, None

    rotation_matrix, _ = cv2.Rodrigues(rotation_vector)
    proj_matrix = np.hstack((rotation_matrix, translation_vector))
    _, _, _, _, _, _, euler_angles = cv2.decomposeProjectionMatrix(proj_matrix)

    # Euler angles in degrees: pitch (x-axis), yaw (y-axis), roll (z-axis)
    pitch = float(euler_angles[0])
    yaw = float(euler_angles[1])
    roll = float(euler_angles[2])
    return pitch, yaw, roll

def detect_head_drooping(face_landmarks, frame):
    """
    Detect head drooping by computing the head pose and using a smoothed pitch value.
    If the smoothed pitch exceeds the threshold, prints a message to the terminal.
    (New Addition)
    """
    global last_head_droop
    pitch, yaw, roll = get_head_pose(face_landmarks.landmark, frame.shape)
    if pitch is not None:

        # Print the Euler angles every frame so you can see them:

        if pitch > -160 and pitch < -100:
            current_time = time.time()

            if last_head_droop is None:
                last_head_droop = current_time
                print("Head droop started")

            elif current_time - last_head_droop > HEAD_DROOP_DURATION:
                   print("it's time")
                   print(current_time - last_head_droop)
                   handle_sleepy_user()
                   last_head_droop = None
        else:
            last_head_droop = None
            if last_head_droop is not None:
                print("Head drop ended")

def play_sound(sound):
    sound = AudioSegment.from_file("../../../../assets/audios/"+sound+".mp3", format="mp3")
    return simpleaudio.play_buffer(
        sound.raw_data,
        num_channels=sound.channels,
        bytes_per_sample=sound.sample_width,
        sample_rate=sound.frame_rate
    )

def handle_alert():
    last_alert = 0
    sound = AudioSegment.from_file("Alarm.mp3", format="mp3")
    play_obj = None
    user_interacted = False
    start_time = time.time()

    while not user_interacted:
        current_time = time.time()
        if current_time-last_alert > 8.5:
            last_alert = current_time
            play_obj = play_sound("Alarm")
        if current_time-start_time > 3:
            user_interacted = True #Mock this for now


    if play_obj:
        play_obj.stop()

def handle_sleepy_user():
    global has_queried
    if not has_queried:
        has_queried = True
        new_speech_handler.say("Driver, please provide a status update, any response is okay")
        response = new_speech_handler.listen_for_response()
        if not response:
            handle_alert()

with mp_face_mesh.FaceMesh(
    max_num_faces=1,
    refine_landmarks=True,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5) as face_mesh:

  while cap.isOpened():
    has_queried = False
    success, image = cap.read()
    if not success:
      print("Ignoring empty camera frame.")
      # If loading a video, use 'break' instead of 'continue'.
      continue

    image.flags.writeable = False
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    results = face_mesh.process(image)
    multi_face_landmarks = results.multi_face_landmarks
    current_time = time.time()

    image.flags.writeable = True
    image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
    if multi_face_landmarks:
      for face_landmarks in multi_face_landmarks:
        detect_head_drooping(face_landmarks, image)
        ear_avg = compute_avg_EAR(face_landmarks)

        if ear_avg < EAR_THRESHOLD:
            if eye_closed_start_time is None:
                eye_closed_start_time = current_time
                print("Started blinking")
                play_sound("Ping")
        else:
            if eye_closed_start_time:
                blink_duration = current_time - eye_closed_start_time
                #print("Blink duration", blink_duration)
                if blink_duration > EXTENDED_BLINK_DURATION:
                    print("Extended Blink Detected!")
                    handle_sleepy_user()
                eye_closed_start_time = None

        mp_drawing.draw_landmarks(
            image=image,
            landmark_list=face_landmarks,
            connections=mp_face_mesh.FACEMESH_TESSELATION,
            landmark_drawing_spec=None,
            connection_drawing_spec=mp_drawing_styles
            .get_default_face_mesh_tesselation_style())

        mp_drawing.draw_landmarks(
            image=image,
            landmark_list=face_landmarks,
            connections=mp_face_mesh.FACEMESH_CONTOURS,
            landmark_drawing_spec=None,
            connection_drawing_spec=mp_drawing_styles
            .get_default_face_mesh_contours_style())

        mp_drawing.draw_landmarks(
            image=image,
            landmark_list=face_landmarks,
            connections=mp_face_mesh.FACEMESH_IRISES,
            landmark_drawing_spec=None,
            connection_drawing_spec=mp_drawing_styles
            .get_default_face_mesh_iris_connections_style())

    cv2.imshow('MediaPipe Face Mesh', cv2.flip(image, 1))
    if cv2.waitKey(5) & 0xFF == 27:
      break
cap.release()
