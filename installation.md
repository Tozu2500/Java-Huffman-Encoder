# Installation and setup for Windows

## Prerequisites
Java 21 or later installed and available on your system PATH

## Using the Batch Installer

1. Clone or download this repository to your computer
2. Navigate to the project directory
3. Double-click `installer.bat` to run the installation
   - You will be prompted to confirm the installation
   - A desktop shortcut will be created at: `C:\Users\[YourUsername]\Desktop\Huffman Encoder.lnk`
4. Once installation completes, use the **Huffman Encoder** shortcut on your desktop to launch the application

## Troubleshooting

- **"Java is not recognized"**: Java 21+ is not installed or not in your system PATH. Install Java from [oracle.com](https://www.oracle.com/java/technologies/downloads/) and add it to your PATH
- **Application doesn't start**: Ensure `bin/` and `lib/` directories exist in the project folder with compiled classes and required JARs
- **Installation failed to create shortcut**: Try running `installer.bat` as Administrator
- **Console window closes immediately**: Check the error message before it closes, or run the batch file from Command Prompt to see detailed error logs

## Alternative: Manual Launch

To run without creating a desktop shortcut, double-click `launch.bat` directly in the project folder.

Or open Command Prompt in the project directory and run:
```cmd
javaw -cp "bin;lib/*" huffman.Main
```