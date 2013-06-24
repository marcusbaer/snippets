//package my;

// http://www.javaeditor.org/index.php?title=Java-Editor/de

import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CopyImage {

  static final int      MAX_DEPTH  = 20;  // Max 20 levels (directory nesting)
  static final String   INDENT_STR = "   ";                 // Single indent.
  static final String[] INDENTS    = new String[MAX_DEPTH]; // Indent array.
  static final String[] runtimePath= new String[MAX_DEPTH];
  private static final int IMG_WIDTH = 1200;
  private static final int IMG_HEIGHT = 1200;
  static String sourcePath = null;
  static String targetPath = null;

  /**
  * @param args
  */
  public static void main(String[] args) {
    //    System.out.println("Welcome");
    //    System.out.println(args[0]);
    //... Initialize array of indentations.
    INDENTS[0] = INDENT_STR;
    for (int i = 1; i < MAX_DEPTH; i++) {
      INDENTS[i] = INDENTS[i-1] + INDENT_STR;
    }
    
      copy(args[0],args[1]);
//    copy("C:/Temp/copy-image/source","C:/Temp/copy-image/target");

//    System.out.print("Enter a directory name to read images recursively:");
//    Scanner in = new Scanner(System.in);
//    System.out.print("Enter a directory name as target for thumbnails:");
//    String out = System.in.toString();

  }

  public static void copy (String sourceDir, String targetDir) {
    sourcePath = sourceDir;
    targetPath = targetDir;
    Scanner in = new Scanner(sourceDir);
    File root = new File(in.nextLine());
    if (root != null && root.isDirectory()) {
      copyRecursively(root, 0);
    } else {
      System.out.println("Not a directory: " + root);
    }
  }

  private static String getRuntimePath () {
    String s = "";
    for (int i=1;i<runtimePath.length;i++) {
      if (runtimePath[i] != null) s = s + runtimePath[i] + "/";
    }
    return s;
  }

  public static void copyRecursively(File fdir, int depth) {
//    System.out.println(getRuntimePath());
    System.out.println(INDENTS[depth] + fdir.getName());  // Print name.

    File toPath = new File(targetPath + "/" + getRuntimePath());
    if (!toPath.exists()) toPath.mkdir();

    // DO THE COPY OF FILES
    int idx = fdir.getName().lastIndexOf(".");
    String s = "";
    if (idx>0) {
        s = fdir.getName().substring(idx+1).toLowerCase();
    }
    if (fdir.isFile() && s.equals("jpg")) {
       try {
           copyImage(sourcePath + "/" + getRuntimePath() + fdir.getName(), targetPath + "/" + getRuntimePath() + fdir.getName());
//           copyFile(sourcePath + "/" + getRuntimePath() + fdir.getName(), targetPath + "/" + getRuntimePath() + fdir.getName());
       } catch (IOException e) {
            System.err.println(e.getMessage());
       }
    }

    if (fdir.isDirectory() && depth < MAX_DEPTH) {
       runtimePath[depth] = fdir.getName();
       for (File f : fdir.listFiles()) {  // Go over each file/subdirectory.
         copyRecursively(f, depth+1);
       }
       runtimePath[depth] = null;
    }
  }

  public static void copyImage(String fromFileName, String toFileName) throws IOException {
    try {
//      BufferedImage image = ImageIO.read(getClass().getResource(fromFileName));
      BufferedImage originalImage = ImageIO.read(new File(fromFileName));
      int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
      BufferedImage resizeImageJpg = resizeImage(originalImage, type);
//      BufferedImage resizeImageJpg = resizeImageWithHint(originalImage, type);
      ImageIO.write(resizeImageJpg, "jpg", new File(toFileName));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void copyFile(String fromFileName, String toFileName)
      throws IOException {
    File fromFile = new File(fromFileName);
    File toFile = new File(toFileName);

    if (!fromFile.exists())
      throw new IOException("FileCopy: " + "no such source file: "
          + fromFileName);
    if (!fromFile.isFile())
      throw new IOException("FileCopy: " + "can't copy directory: "
          + fromFileName);
    if (!fromFile.canRead())
      throw new IOException("FileCopy: " + "source file is unreadable: "
          + fromFileName);

    if (toFile.isDirectory())
      toFile = new File(toFile, fromFile.getName());

    if (toFile.exists()) {
      if (!toFile.canWrite())
        throw new IOException("FileCopy: "
            + "destination file is unwriteable: " + toFileName);
      System.out.print("Overwrite existing file " + toFile.getName()
          + "? (Y/N): ");
      System.out.flush();
      BufferedReader in = new BufferedReader(new InputStreamReader(
          System.in));
      String response = in.readLine();
      if (!response.equals("Y") && !response.equals("y"))
        throw new IOException("FileCopy: "
            + "existing file was not overwritten.");
    } else {
      String parent = toFile.getParent();
      if (parent == null)
        parent = System.getProperty("user.dir");
      File dir = new File(parent);
      if (!dir.exists())
        throw new IOException("FileCopy: "
            + "destination directory doesn't exist: " + parent);
      if (dir.isFile())
        throw new IOException("FileCopy: "
            + "destination is not a directory: " + parent);
      if (!dir.canWrite())
        throw new IOException("FileCopy: "
            + "destination directory is unwriteable: " + parent);
    }

    FileInputStream from = null;
    FileOutputStream to = null;
    try {
      from = new FileInputStream(fromFile);
      to = new FileOutputStream(toFile);
      byte[] buffer = new byte[4096];
      int bytesRead;

      while ((bytesRead = from.read(buffer)) != -1)
        to.write(buffer, 0, bytesRead); // write
    } finally {
      if (from != null)
        try {
          from.close();
        } catch (IOException e) {
          ;
        }
      if (to != null)
        try {
          to.close();
        } catch (IOException e) {
          ;
        }
    }
  }
/*
  public void resize(){
    try {
      BufferedImage originalImage = ImageIO.read(new File("c:\\image\\mkyong.jpg"));
      int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

      BufferedImage resizeImageJpg = resizeImage(originalImage, type);
      ImageIO.write(resizeImageJpg, "jpg", new File("c:\\image\\mkyong_jpg.jpg"));

      BufferedImage resizeImagePng = resizeImage(originalImage, type);
      ImageIO.write(resizeImagePng, "png", new File("c:\\image\\mkyong_png.jpg"));

      BufferedImage resizeImageHintJpg = resizeImageWithHint(originalImage, type);
      ImageIO.write(resizeImageHintJpg, "jpg", new File("c:\\image\\mkyong_hint_jpg.jpg"));

      BufferedImage resizeImageHintPng = resizeImageWithHint(originalImage, type);
      ImageIO.write(resizeImageHintPng, "png", new File("c:\\image\\mkyong_hint_png.jpg"));

    } catch(IOException e) {
      System.out.println(e.getMessage());
    }
  }
*/
  private static BufferedImage resizeImage(BufferedImage originalImage, int type){
    int sW = originalImage.getWidth();
    int sH = originalImage.getHeight();
    int tW = 0;
    int tH = 0;
    if (sW<IMG_WIDTH && sH<IMG_HEIGHT) {
       tW = sW;
       tH = sH;
    } else if (sW>sH) {
      tW = IMG_WIDTH;
      tH = tW * sH/sW;
    } else {
      tH = IMG_HEIGHT;
      tW = tH * sW/sH;
    }
    BufferedImage resizedImage = new BufferedImage(tW, tH, type);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(originalImage, 0, 0, tW, tH, null);
    g.dispose();
    return resizedImage;
  }

  private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type){
    int sW = originalImage.getWidth();
    int sH = originalImage.getHeight();
    int tW = 0;
    int tH = 0;
    if (sW<IMG_WIDTH && sH<IMG_HEIGHT) {
       tW = sW;
       tH = sH;
    } else if (sW>sH) {
      tW = IMG_WIDTH;
      tH = tW * sH/sW;
    } else {
      tH = IMG_HEIGHT;
      tW = tH * sW/sH;
    }
    BufferedImage resizedImage = new BufferedImage(tW, tH, type);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(originalImage, 0, 0, tW, tH, null);
    g.dispose();
    g.setComposite(AlphaComposite.Src);

    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING,
    RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    RenderingHints.VALUE_ANTIALIAS_ON);

    return resizedImage;
  }

}