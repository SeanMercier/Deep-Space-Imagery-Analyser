package com.example.assignment1deepspace;

import java.io.File;
import java.security.Key;
import java.util.*;

import java.util.Set;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class Controller {
    public Slider minPixelsInStarSlider;
    @FXML
    private Label starcountlabel;

    @FXML
    private Button starcountbutton;
    public ImageView colorImage;
    public ImageView blackwhiteimagev;
    public ImageView grayscaleimageview;
    public ImageView blueimageview;
    public ImageView greenimageview;
    public ImageView redimageview;
    public Slider sliderbright;
    public HashMap<Integer, ArrayList<Integer>> hashmap1 = new HashMap();
    ColorAdjust colorAdjust = new ColorAdjust();
    public int[] imagearray;

    public Controller() {
    }

    public void onClickChooseFile() { //method is called when the user clicks on a button to select a file
        // Create a new file chooser dialog
        FileChooser fileChooser = new FileChooser();
        // Show dialog and wait for the user to select a file
        File selectedFile = fileChooser.showOpenDialog((Window)null);
        // Check if a file was actually selected
        if (selectedFile != null) {
            //if a file was selected, create a new image object from the file path
            Image chosenImage = new Image(selectedFile.toURI().toString());
            // Get the height and width of the chosen image
            int height = (int)chosenImage.getHeight();
            int width = (int)chosenImage.getWidth();
            // Set the chosen image as the source for two ImageView objects
            this.colorImage.setImage(chosenImage);
            this.blackwhiteimagev.setImage(chosenImage);
        }

    }

    // This method converts a color image to a black and white image
    public void blackAndWhite() {
        // Check that the color and black and white image views are not null
        if (this.colorImage != null && this.blackwhiteimagev != null) {
            // Get the selected image from the color image view
            Image selectedImage = this.colorImage.getImage();
            // Get a PixelReader object for the selected image
            PixelReader pixelReaderbw = selectedImage.getPixelReader();
            // Get the height and width of the selected image
            int height = (int)selectedImage.getHeight();
            int width = (int)selectedImage.getWidth();
            // Create a new int array to hold the image data
            this.imagearray = new int[height * width];
            // Create a new WritableImage object for the black and white image
            WritableImage blackWhiteImage = new WritableImage(width, height);

            // Loop through each pixel in the selected image
            for(int y = 0; y < height; ++y) {
                for(int x = 0; x < width; ++x) {
                    // Get the red, green, and blue values for the current pixel
                    int red = (int)(pixelReaderbw.getColor(x, y).getRed() * 255.0);
                    int green = (int)(pixelReaderbw.getColor(x, y).getGreen() * 255.0);
                    int blue = (int)(pixelReaderbw.getColor(x, y).getBlue() * 255.0);
                    // Check if the pixel is brighter than a threshold value
                    if ((double)red >= this.sliderbright.getValue() && (double)green >= this.sliderbright.getValue() && (double)blue >= this.sliderbright.getValue()) {
                        // If the pixel is brighter, set it to white in the black and white image
                        blackWhiteImage.getPixelWriter().setColor(x, y, Color.WHITE);
                        this.imagearray[y * width + x] = y * width + x;
                    } else {
                        // If the pixel is darker, set it to black in the black and white image
                        blackWhiteImage.getPixelWriter().setColor(x, y, Color.BLACK);
                        this.imagearray[y * width + x] = -1;
                    }
                }
            }
            // Set the black and white image as the source for the black and white image view
            this.blackwhiteimagev.setImage(blackWhiteImage);
            // Perform more image processing operations
            this.unionsets(width, height);
            this.printarray(width, height);
            this.storeInHashMap();
        }
    }

    // This method prints the contents of the imagearray in rows and columns
    public void printarray(int width, int height) {
        // Loop through each element in the imagearray
        for(int i = 0; i < this.imagearray.length; ++i) {
            // Check if the current element is the start of a new row
            if (i % width == 0) {
                // If so, print a new line
                System.out.println("");
            }
            // Get the value of the current element
            int var1 = this.imagearray[i];
            // Print the value of the current element followed by a space
            System.out.print("" + var1 + " ");
        }

    }

    // This function performs a union operation on sets of pixels in an image
    // The width and height of the image are provided as input parameters
    public void unionsets(int width, int height) {
        // Loop through each pixel in the image
        for(int i = 0; i < this.imagearray.length; ++i) {

            // If the pixel is not part of any set, continue to the next pixel
            if (this.imagearray[i] >= 0) {

                // If the next pixel to the right is not part of any set and is not at the end of a row
                // Perform a union operation between the current pixel and the next pixel
                if (i + 1 < this.imagearray.length && i + 1 % width != 0 && this.imagearray[i + 1] >= 0) {
                    UnionFind.union(this.imagearray, i, i + 1);
                }

                // If the pixel below is not part of any set
                // Perform a union operation between the current pixel and the pixel below
                if (i + width < this.imagearray.length && this.imagearray[i + width] >= 0) {
                    UnionFind.union(this.imagearray, i, i + width);
                }
            }
        }

    }

    // This function stores the pixels of an image in a HashMap data structure.
    public void storeInHashMap() {
        // If the HashMap object is not null, clear it to remove any previous values
        if (this.hashmap1 != null) {
            this.hashmap1.clear();
        }

        // Loop through each pixel in the image
        for(int i = 0; i < this.imagearray.length; ++i) {

            // If the pixel is not a background pixel (-1)
            if (this.imagearray[i] != -1) {

                // Find the root of the set that the current pixel belongs to
                int root = UnionFind.find(this.imagearray, i);
                // If the root is not already a key in the HashMap, add it with an empty ArrayList as its value
                if (!this.hashmap1.containsKey(root)) {
                    this.hashmap1.put(root, new ArrayList());
                }

                // Add the current pixel to the ArrayList value associated with the root key in the HashMap
                ((ArrayList)this.hashmap1.get(root)).add(i);
            }
        }

        //Print the resulting HashMap
        System.out.println(this.hashmap1);
    }

    public void displayBlueCircles() {
        // Get the key set of the HashMap containing the celestial objects
        Set<Integer> keySet = this.hashmap1.keySet();

        // Initialize a counter for the number of blue circles
        int blueCirclesCount = 0;

        // Loop through each celestial object in the HashMap
        for (Integer hashRoot : keySet) {
            // Print the root of the current celestial object and its associated pixels
            System.out.println("" + hashRoot + " " + this.hashmap1.get(hashRoot));

            // Get the ArrayList of pixels associated with the current celestial object
            ArrayList<Integer> rootList = (ArrayList)this.hashmap1.get(hashRoot);

            // Get the width and height of the image
            int width = (int)this.colorImage.getImage().getWidth();
            int height = (int)this.colorImage.getImage().getHeight();

            // Initialize variables for the initial and last x and y coordinates of the celestial object
            int initialX = Integer.MAX_VALUE;
            int initialY = Integer.MAX_VALUE;
            int lastX = 0;
            int lastY = 0;

            // Loop through each pixel in the current celestial object and update the initial and last x and y coordinates as needed
            for(int i = 0; i < rootList.size(); ++i) {
                if (rootList.get(i) % width < initialX) {
                    initialX = rootList.get(i) % width;
                }

                if (rootList.get(i) / width < initialY) {
                    initialY = rootList.get(i) / width;
                }

                if (rootList.get(i) % width > lastX) {
                    lastX = rootList.get(i) % width;
                }

                if (rootList.get(i) / width > lastY) {
                    lastY = rootList.get(i) / width;
                }
            }

            // Get the scale factors for the image size and the display size
            Bounds bounds = this.colorImage.getLayoutBounds();
            double XScale = bounds.getWidth() / this.colorImage.getImage().getWidth();
            double YScale = bounds.getHeight() / this.colorImage.getImage().getHeight();

            // Scale the initial and last x and y coordinates to the display size
            initialX = (int)((double)initialX * XScale);
            initialY = (int)((double)initialY * YScale);
            lastX = (int)((double)lastX * XScale);
            lastY = (int)((double)lastY * YScale);

            // Calculate the center coordinates and radius of the blue circle based on the scaled coordinates
            int midX = initialX + (lastX - initialX) / 2;
            int midY = initialY + (lastY - initialY) / 2;
            Circle circle = new Circle();
            circle.setCenterX(midX);
            circle.setCenterY(midY);
            circle.setRadius(Math.max(midX - initialX, midY - initialY));
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.BLUE);
            circle.setStrokeWidth(1.0);
            circle.setTranslateX(this.colorImage.getLayoutX());
            circle.setTranslateY(this.colorImage.getLayoutY());

            // Add the blue circle to the parent node of the image
            ((Pane)this.colorImage.getParent()).getChildren().add(circle);

            // add tooltip to display the size of the celestial object
            circle.setOnMouseEntered(event -> {
                int order = hashmap1.get(hashRoot).size();

                Tooltip tooltip = new Tooltip("Size: " + order);
                Tooltip.install(circle, tooltip);

            });
            blueCirclesCount++;
        }
        //Print out on a label the number of circles
        starcountlabel.setText("Number Of Stars: " + blueCirclesCount);
    }

    // This method assigns a number to each circle object displayed on the image.
    public void numberCircles() {

        ArrayList<Circle> circleList = new ArrayList();
        for (Object o : ((Pane)this.colorImage.getParent()).getChildren())

        if (o instanceof Circle)
            circleList.add((Circle) o);

        //sort circles from smallest to biggest, largest circle is given value 0

        Collections.sort(circleList, (a, b) -> (int)(b.getRadius() - a.getRadius()));
        System.out.println(circleList);
        int count = 1;

        // Adds a Text object displaying the number to each circle and displays it on the image.
        for (Circle c: circleList){
            Text numm = new Text(c.getCenterX(), c.getCenterY(), count +"");
            numm.setStroke(Color.RED);
            numm.setTranslateX(this.colorImage.getLayoutX());
            numm.setTranslateY(this.colorImage.getLayoutY());
            ((Pane)this.colorImage.getParent()).getChildren().add(numm);
            count++;

            //Stop adding numbers once 25 circles have been numbered
            if (count > 25) {
                break;
            }
        }
    }

    //Resets/removes the numbers of the circles from the GUI
    public void resetNumCircles() {
        Pane colorImagePane = (Pane) colorImage.getParent();
        colorImagePane.getChildren().removeIf(node -> node instanceof Text);
    }

    //Deletes all the instances of Circle from the GUI
    public void resetAllCircles(){
        Pane colorImagePane = (Pane) colorImage.getParent();
        colorImagePane.getChildren().removeIf(node -> node instanceof Circle);
    }

    //Resets all the desired sliders, labels, and image effects to their original state
    public void resetAll() {
        this.sliderbright.setValue(127.5);
        this.hashmap1.clear();
        this.imagearray = null;
        this.starcountlabel.setText("Number Of Stars: 0");
        this.colorAdjust.setBrightness(0.0);
        this.colorImage.setEffect(null);
        resetAllCircles();
        resetNumCircles();
    }

    // Define a method to get the sizes of all disjoint sets in the hashmap
    public int[] getDisjointSetSizes() {
        PixelReader imageReader = colorImage.getImage().getPixelReader();
        // Create an array list to store the sizes of disjoint sets
        ArrayList<Integer> sizes = new ArrayList<Integer>();

        // Loop through all keys in the hashmap
        for (int key : hashmap1.keySet()) {

            // Find the root of the key using the UnionFind class
            int root = UnionFind.find(imagearray, key);

            // If the root is not already in the sizes array, add it
            if (!sizes.contains(root)) {
                sizes.add(root);
            }
        }

        // Create an integer array to store the sizes of disjoint sets
        int[] setSizes = new int[sizes.size()];

        // Loop through all the root keys in the sizes array
        for (int i = 0; i < sizes.size(); i++) {

            // Get the root value from the sizes array
            int root = sizes.get(i);
            int size = 0;
            float red = 0;
            float green = 0;
            float blue = 0;

            // Loop through all the keys in the hashmap
            for (int key : hashmap1.keySet()) {
                // If the root of the key is equal to the root value from the sizes array
                if (UnionFind.find(imagearray, key) == root) {
                    // Add the size of the array list for that key to the size variable
                    size += hashmap1.get(key).size();
                }
                Color color = imageReader.getColor(size % (int) colorImage.getImage().getWidth(), size / (int) colorImage.getImage().getWidth());
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
            }


            // Store the size value in the setSizes array at the current index
            setSizes[i] = size;

            // Print the size of the current disjoint set as well as the percentage of each type of gas in the star
            System.out.println("Size of disjoint set " + root + ": " + size + " pixels. " + "Estimated sulphur: " + red + " Estimated hydrogen: " + green + " Estimated oxygen: " + blue);
        }

        // Return the setSizes array
        return setSizes;
    }

    // Reduces noise from the image by removing small celestial objects
    public void reduceNoise() {
        Iterator<Map.Entry<Integer, ArrayList<Integer>>> iterator = hashmap1.entrySet().iterator();
        // Get the width and height of the image
        int width = (int) blackwhiteimagev.getImage().getWidth();
        int height = (int) blackwhiteimagev.getImage().getHeight();

        // Create a new writable image to store the image after noise reduction
        WritableImage imageAfterNoiseReduction = new WritableImage(width, height);
        // Get a pixel writer for the writable image
        PixelWriter pixelWriter = imageAfterNoiseReduction.getPixelWriter();

        // Loop through each entry in the hashmap
        while (iterator.hasNext()) {

            // Get the entry and the celestial objects list
            Map.Entry<Integer, ArrayList<Integer>> entry = iterator.next();
            List<Integer> celestialObjects = entry.getValue();

            // Get the number of pixels in the celestial object
            int numberOfPixels = celestialObjects.size();

            // Check if the number of pixels is smaller than the minimum or larger than 500
            if (numberOfPixels < minPixelsInStarSlider.getValue() || numberOfPixels > 500) {
                // Set the color of each pixel in the celestial object to black
                for (int pixel : celestialObjects) {
                    pixelWriter.setColor(pixel % width, pixel / width, Color.BLACK);
                }
                // Remove the celestial object from the hashmap
                iterator.remove();
                }
            }
        //call setBlack method to keep black background black
        makeBlack(imageAfterNoiseReduction, pixelWriter);
    }

    // Method to color the disjoint sets of pixels with random colors
    public void colorDisjointSets() {
        // Get the width and height of the black and white image
        int width = (int) blackwhiteimagev.getImage().getWidth();
        int height = (int) blackwhiteimagev.getImage().getHeight();

        // Get a list of all the sets of pixels
        List<List<Integer>> pixels = new ArrayList<>(hashmap1.values());

        // Create a new writable image of the same size as the original image
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // For each set of pixels, assign a random color and color all the pixels in that set with the same color
        for (List<Integer> value : pixels) {
            Color color = Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
            for (int pixel : value) {
                pixelWriter.setColor(pixel % width, pixel/width, color);
            }
        }
        //call setBlack method to keep black background black
        makeBlack(writableImage, pixelWriter);
    }

    //Sets the black pixels in the image, where the corresponding index in the imagearray is -1.
    public void makeBlack(Image image, PixelWriter pixelWriter) {
        for (int i = 0; i < imagearray.length; i++) {
            // skip pixels that are not black in the imagearray
            if (imagearray[i] != -1) {
                continue;
            }
            // set the pixel at the corresponding index to black in the image using PixelWriter
            pixelWriter.setColor((int) (i % image.getWidth()), (int) (i / image.getWidth()), Color.BLACK);
        }
        // update the black and white image view with the modified image
        blackwhiteimagev.setImage(image);
    }

}
