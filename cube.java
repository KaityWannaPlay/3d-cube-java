import java.util.Arrays;

public class RotatingCube {
    // Rotation angles for the cube
    static float A = 0.0f, B = 0.0f, C = 0.0f;
    // Width and height of the terminal window
    static final int width = 160, height = 44;
    // Z-buffer for depth calculations
    static float[][] zBuffer = new float[width][height];
    // Buffer for storing ASCII characters
    static char[][] buffer = new char[width][height];
    // ASCII character for the background
    static char backgroundASCIICode = ' ';
    // Distance from the camera to the cube
    static int distanceFromCam = 100;
    // Horizontal offset for projection
    static float horizontalOffset = 0;
    // Projection constant
    static float K1 = 40;
    // Rotation speed (adjust as needed)
    static float incrementSpeed = 0.07f;

    // 3D coordinates
    static float x, y, z;
    // One over z (reciprocal of z)
    static float ooz;
    // Screen coordinates
    static int xp, yp;

    // Calculate X coordinate after projection
    static float calculateX(float i, float j, float k) {
        return j * (float)Math.sin(A) * (float)Math.sin(B) * (float)Math.cos(C) -
               k * (float)Math.cos(A) * (float)Math.sin(B) * (float)Math.cos(C) +
               j * (float)Math.cos(A) * (float)Math.sin(C) +
               k * (float)Math.sin(A) * (float)Math.sin(C) +
               i * (float)Math.cos(B) * (float)Math.cos(C);
    }

    // Calculate Y coordinate after projection
    static float calculateY(float i, float j, float k) {
        return j * (float)Math.cos(A) * (float)Math.cos(C) +
               k * (float)Math.sin(A) * (float)Math.cos(C) -
               j * (float)Math.sin(A) * (float)Math.sin(B) * (float)Math.sin(C) +
               k * (float)Math.cos(A) * (float)Math.sin(B) * (float)Math.sin(C) -
               i * (float)Math.cos(B) * (float)Math.sin(C);
    }

    // Calculate Z coordinate after projection
    static float calculateZ(float i, float j, float k) {
        return k * (float)Math.cos(A) * (float)Math.cos(B) -
               j * (float)Math.sin(A) * (float)Math.cos(B) +
               i * (float)Math.sin(B);
    }

    // Calculate and render a surface of the cube
    static void calculateForSurface(float cubeX, float cubeY, float cubeZ, char ch) {
        x = calculateX(cubeX, cubeY, cubeZ);
        y = calculateY(cubeX, cubeY, cubeZ);
        z = calculateZ(cubeX, cubeY, cubeZ) + distanceFromCam;

        if (z == 0) {
            z = 1e-6f; // Avoid division by zero
        }

        ooz = 1 / z;

        xp = (int)(width / 2 + horizontalOffset + K1 * ooz * x * 2);
        yp = (int)(height / 2 + K1 * ooz * y);

        if (xp >= 0 && xp < width && yp >= 0 && yp < height) {
            if (ooz > zBuffer[xp][yp]) {
                zBuffer[xp][yp] = ooz;
                buffer[xp][yp] = ch;
            }
        }
    }

    // Clear the buffer with background character
    static void clearBuffer() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                buffer[i][j] = backgroundASCIICode;
                zBuffer[i][j] = 0;
            }
        }
    }

    // Display the rendered frame
    static void showBuffer() {
        // ANSI escape to move cursor to home position
        System.out.print("\033[H");
        
        StringBuilder frame = new StringBuilder();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                frame.append(buffer[i][j]);
            }
            frame.append('\n');
        }
        System.out.print(frame);
    }

    public static void main(String[] args) {
        // ANSI escape to clear the screen
        System.out.print("\033[2J");
        
        // Initialize buffers
        clearBuffer();

        while (true) {
            clearBuffer();

            // Rotate the cube
            A += incrementSpeed;
            B += incrementSpeed;
            C += 0.01;

            // Render the cubes
            for (float cubeX = -20; cubeX < 20; cubeX += 0.5) {
                for (float cubeY = -20; cubeY < 20; cubeY += 0.5) {
                    calculateForSurface(cubeX, cubeY, -20, '@');
                    calculateForSurface(20, cubeY, cubeX, '$');
                    calculateForSurface(-20, cubeY, -cubeX, '~');
                    calculateForSurface(-cubeX, cubeY, 20, '#');
                    calculateForSurface(cubeX, -20, -cubeY, ';');
                    calculateForSurface(cubeX, 20, cubeY, '+');
                }
            }

            showBuffer();

            // Sleep to control frame rate
            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
