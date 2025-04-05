import pyttsx3, speech_recognition
tts_engine = pyttsx3.init()

class main():
    def say(self, speech):
        tts_engine.say(speech)
        tts_engine.runAndWait()

    def listen_for_response(self):
        recognizer = speech_recognition.Recognizer()
        try:
            with speech_recognition.Microphone() as source:
                print("Listening for response")
                audio = recognizer.listen(source, timeout=5)
        except Exception as e:
            print(f"Error accessing microphone: {e}")
            return None

        try:
            response = recognizer.recognize_google(audio)
            print(response)
            return response.lower()
        except Exception as e:
            print(f"Error recognizing speech: {e}")
            return "Placeholder"



