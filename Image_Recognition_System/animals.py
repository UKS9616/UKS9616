import streamlit as st
import tensorflow as tf
import numpy as np
from PIL import Image
import io
import cv2

# Load labels
def load_labels(label_file):
    try:
        with open(label_file, "r") as f:
            labels = [line.strip() for line in f.readlines()]
        return labels
    except Exception as e:
        st.error(f"Error loading labels: {e}")
        return []

# Function to load model and make predictions, returning both index and confidence score
def model_prediction(image):
    model = tf.keras.models.load_model("trained_model.h5", compile=False)
    image = image.resize((64, 64))  # Resize image to match model's expected sizing
    input_arr = tf.keras.preprocessing.image.img_to_array(image)  # Convert to array
    input_arr = np.array([input_arr])  # Convert single image to batch
    predictions = model.predict(input_arr)
    confidence_scores = tf.nn.softmax(predictions[0]).numpy()  # Convert logits to probabilities
    result_index = np.argmax(confidence_scores)
    confidence_score = confidence_scores[result_index]
    return result_index, confidence_score

# Function to capture an image from the webcam
def capture_image_from_webcam():
    cap = cv2.VideoCapture(0)  # Open the webcam
    if not cap.isOpened():
        st.error("Could not access the webcam.")
        return None
    ret, frame = cap.read()  # Capture frame-by-frame
    cap.release()
    if not ret:
        st.error("Failed to capture image.")
        return None
    
    frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)  # Convert BGR to RGB
    image = Image.fromarray(frame_rgb)  # Convert to PIL Image
    return image

# Sidebar
st.sidebar.title("Dashboard")
app_mode = st.sidebar.selectbox("Select Page", ["Home", "Prediction", "About Project"])

# Main Page
if app_mode == "Home":
    st.header("IMAGE RECOGNITION SYSTEM")
    image_path = "animals.jpg"
    st.image(image_path, use_column_width=True)

# Prediction Page
elif app_mode == "Prediction":
    st.header("Model Prediction")
    use_webcam = st.checkbox("Use Webcam")

    if use_webcam:
        if st.button("Capture Image"):
            captured_image = capture_image_from_webcam()
            if captured_image:
                st.image(captured_image, caption='Captured Image', use_column_width=True)
                labels = load_labels("labels.txt")
                result_index, confidence_score = model_prediction(captured_image)
                if result_index is not None and result_index < len(labels):
                    st.success(f"Model predicts: {labels[result_index]} with {confidence_score:.3%} confidence.")
                else:
                    st.error("Prediction failed or labels not loaded correctly.")
    else:
        test_image = st.file_uploader("Select an Image")
        if test_image is not None:
            image = Image.open(test_image).convert('RGB')
            st.image(image, caption='Uploaded Image.', use_column_width=True)
            if st.button("Predict"):
                st.balloons()
                labels = load_labels("labels.txt")
                result_index, confidence_score = model_prediction(image)
                if result_index is not None and result_index < len(labels):
                    st.success(f"Model predicts: {labels[result_index]} with {confidence_score:.2%} confidence.")
                else:
                    st.error("Prediction failed or labels not loaded correctly.")

# About Project
elif app_mode == "About Project":
    st.header("About Project")
    st.write("This is an image recognition system that recognizes animals.")
    st.text("This dataset contains the images of following animals:")
    st.code("antelope, badger, bat, bear, bee, beetle, bison, boar, butterfly, cat, caterpillar, chimpanzee, cockroach, cow, coyote, crab, crow, deer, dog, dolphin, donkey, dragonfly, duck, eagle, elephant, flamingo, fly, fox, goat, goldfish, goose, gorilla, grasshopper, hamster, hare, hedgehog, hippopotamus, hornbill, horse, hummingbird, hyena, jellyfish, kangaroo, koala, ladybugs, leopard, lion, lizard, lobster, mosquito, moth, mouse, octopus, okapi, orangutan, otter, owl, ox, oyster, panda, parrot, pelecaniformes, penguin, pig, pigeon, porcupine, possum, raccoon, rat, reindeer, rhinoceros, sandpiper, seahorse, seal, shark, sheep, snake, sparrow, squid, squirrel, starfish, swan, tiger, turkey, turtle, whale, wolf, wombat, woodpecker, zebra")
    st.subheader("Content")
    st.text("In this Dataset, we have 5400 Animal Images in 90 different categories or classes.")
