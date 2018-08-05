/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Image_steganography;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 *
 * @author locnqt
 */
public class Ste {

    public boolean Encode(String image_path, String secret, String output_path) {
        try {
            int count = 0;
            //Lay duoi file
            String fileType = image_path.substring(image_path.lastIndexOf('.') + 1);
            System.out.println("type: " + fileType);
            String newfiletype = "." + fileType;
            //dam bao duoi file save la png or bmp
            if (!output_path.endsWith(newfiletype)) {
                if (output_path.indexOf(".") != -1) {
                    output_path = output_path.substring(0, output_path.lastIndexOf('.'));  
                }
            }
            System.out.println("out file: " + output_path);

            String secret_bin = messtobin(secret) + "000000000"; //them 9bit 0 vao cuoi message de danh dau ket thuc
            while (secret_bin.length() % 9 != 0) { // dam bao moi lan du 3 bit cho RGB
                secret_bin += "0";
            }
            System.out.println("secret_bin: " + secret_bin);
            char[] binHidden = secret_bin.toCharArray(); //chuyen doi secret_bin thanh mang char binHidden

            BufferedImage buff = ImageIO.read(new File(image_path)); //doc anh vao buffe
            for (int x = 0; x < buff.getWidth(); x++) {
                for (int y = 0; y < buff.getHeight(); y++) {
                    int rgb = buff.getRGB(x, y); //lay gia tri RGB tai toa do x,y  
                    //o and may =0 // 1 or may =1
                    rgb = (binHidden[count++] == '0') ? (rgb & 0xFFFEFFFF) : (rgb | 0x00010000); //thay the gia tri LSB Red 
                    rgb = (binHidden[count++] == '0') ? (rgb & 0xFFFFFEFF) : (rgb | 0x00000100); //thay the gia tri LSB Green 
                    rgb = (binHidden[count++] == '0') ? (rgb & 0xFFFFFFFE) : (rgb | 0x00000001); //thay the gia tri LSB Blue 
                    buff.setRGB(x, y, rgb); //cap nhat lai gia tri RGB
                    if (count == binHidden.length) {
                        break;
                    }
                }
                if (count == binHidden.length) {
                    break; //break vong lap khi da giau het cac bit trong binHidden
                }
            }
            if (saveImage(buff, fileType, output_path)) //luu lai anh moi da giau tin
            {
                System.out.println("Save file OK!");
                return true;
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        return false;
    }
    // Lay ra các bit LSB  da giau trong pixel trong anh
    public String getLSBBits(int pixel_rgb) { 
        Color c = new Color(pixel_rgb);
        String result = "";
        // toBinaryString(int value) của lớp Integer trong JAVA dùng để lấy chuỗi nhị phân từ biến value
        //vd toBinaryString(170) = 10101010
        String redInB = Integer.toBinaryString(c.getRed());
        String greenInB = Integer.toBinaryString(c.getGreen());
        String blueInB = Integer.toBinaryString(c.getBlue());
        //cat lay bit LSB trong RGB 
        result += String.valueOf(redInB.charAt(redInB.length() - 1)); 
        result += String.valueOf(greenInB.charAt(greenInB.length() - 1));
        result += String.valueOf(blueInB.charAt(blueInB.length() - 1));
        return result;
    }

    public String Decode(String image_path) {
        try {
            String secret_bin = ""; //chuoi binary thong diep da giau
            boolean endfalg = false; 
            int count = 0; //count de dem bit LSB da lay duoc
            int ktra9bit0 = 0;
            String secret="";
            BufferedImage buff = ImageIO.read(new File(image_path));

            for (int x = 0; x < buff.getWidth(); x++) {
                for (int y = 0; y < buff.getHeight(); y++) {
                    secret = getLSBBits(buff.getRGB(x, y)); //lay 3 bit LSB tren tung pixel
                    secret_bin = secret_bin+secret;
                    // ktra 9 bit 0 cuoi thong diep de ket thuc vong lap
                    if(secret.equals("000")){
                        ktra9bit0 +=3;
                        count +=3;
                    }
                    else{
                        ktra9bit0=0;
                        count+=3;
                    }
                    if(count==9){
                        if (ktra9bit0 == 9) {
                            endfalg = true;
                            break;
                        }
                        ktra9bit0 = 0;
                        count = 0;
                    }
                }
                if(endfalg) break;
            }
            String mess = ""; //Thong diep
//            System.out.println("s leng: "+secret_bin.length());
//            System.out.println(secret_bin);
            secret_bin = secret_bin.substring(0, (secret_bin.length() - 9)); // cat 9 bit 0 cuoi message
//            System.out.println("s leng: "+secret_bin.length());
//            System.out.println(secret_bin);
            int odd0 = secret_bin.length() % 8;
            System.out.println("ood0: "+odd0);
            secret_bin = secret_bin.substring(0, secret_bin.length()-odd0); // cat bit du da them
//            System.out.println("s leng: "+secret_bin.length());
//            System.out.println(secret_bin);
            for (int i = 0; i < secret_bin.length(); i = i + 8) { //moi lan doc 1 byte
                mess += bintomess(secret_bin.substring(i, i + 8));
            }
            System.out.println("Message: " + mess);
            return mess;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        return "";
    }
    
    public boolean saveImage(BufferedImage buff, String file_type, String file_path) {
        try {
            if (!file_path.endsWith("." + file_type)) {
                return ImageIO.write(buff, file_type, new File(file_path + "." + file_type));
            } else {
                return ImageIO.write(buff, file_type, new File(file_path));
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        return false;
    }
    
    public static String messtobin(String mess) {
        String result = "";
        for (char c : mess.toCharArray()) {
            String bin = Integer.toBinaryString(c);
            while (bin.length() < 8) { //them 0 vao dau cho du 8 bit
                bin = "0" + bin;
            }
            result += bin;
        }
        System.out.println("result: " + result);
        return result;
    }

    public static String bintomess(String bin) {
        int charCode = Integer.parseInt(bin, 2); //doi bin thanh decimal vd:10101010 -> 170
        String result = new Character((char) charCode).toString(); // doi thanh ky tu
        return result;
    }
}
