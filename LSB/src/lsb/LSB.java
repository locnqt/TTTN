/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lsb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Nguyen Quang Tuan Loc _ N14DCAT097 LSB 24bit
 */
public class LSB {

    /**
     * @param args the command line arguments
     */
    private static int HEADER_SIZE = 54;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        int n = 1;
        while (n != 3) {
            try {
                System.out.println("---------------------------------------------------------------");
                System.out.print(" 1.Encode \n 2.Decode \n 3.Exit \n choose(1-3):");
                n = sc.nextInt();
                switch (n) {
                    case 1:
                        Encode();
                        break;
                    case 2:
                        Decode();
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("ERROR! You must choose 1->3!");
                }
            } catch (java.util.InputMismatchException ioe) {
                System.err.println("!!ERROR! You must choose 1->3!!");
                break;
            }
        }
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
        System.out.println("result: "+result);
        return result;
    }

    public static String bintomess(String bin) {
        int charCode = Integer.parseInt(bin, 2);
        System.out.println("char: "+charCode);
        String result = new Character((char) charCode).toString();
        return result;
    }

    public static void Encode() throws IOException {
        System.out.print("Enter you message: ");
        sc.nextLine();
        String mess = sc.nextLine();
        String bin = messtobin(mess) + "00000000"; //them null vao cuoi message de danh dau ket thuc

        System.out.print("Enter bitmap file name: ");
        String srcname = sc.nextLine();

        File f = new File(srcname + ".bmp");
        BufferedImage bufferedImage = ImageIO.read(f);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "bmp", bos);
        byte[] arr = bos.toByteArray();

        int data = HEADER_SIZE;
        char temp;
        int x = 0;
        System.out.println("giau dc: "+(arr.length - HEADER_SIZE)+" ky tu");
        if (bin.length() > (arr.length - HEADER_SIZE)) {
            System.out.println("message too large for image to hide!");
        } else {
            for (int i = 0; i < bin.length(); i++) {
                arr[data] >>= 1;
                arr[data] <<= 1;
                arr[data] += (bin.charAt(i) - 48);   //
                data++;
                System.out.print(bin.charAt(i) - 48);
            }
        }
//        System.out.println("");
        System.out.print("Enter name file you want to save encrypted image: ");
        String destname = sc.nextLine();

        File output = new File(destname + ".bmp");
        bufferedImage = ImageIO.read(new ByteArrayInputStream(arr));
        if (ImageIO.write(bufferedImage, "bmp", output)) {
            System.out.println("Save file OK!");
        } else {
            System.out.println("ERROR!! Cannot save!!");
        }
    }

    public static void Decode() throws IOException {
        try {
            sc.nextLine();
            System.out.print("Enter bitmap file name you want to decrypt: ");
            String destname = sc.nextLine();

            File f = new File(destname + ".bmp");
            BufferedImage bufferedImage = ImageIO.read(f);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "bmp", bos);
            byte[] arr = bos.toByteArray();

            int data = HEADER_SIZE;
            String databin = "";
            String mess = "";
            int ktra8bit0 = 0; // kiem tra 8 bit 0 da duoc danh dau cuoi message
            int count = 0; // dem bit, du 8 bit thanh 1 byte --> ktra8bit=0;
            while (true) {
                if (arr[data] % 2 == 0) {
                    databin += "0";
                    ktra8bit0 += 1;
                    count++;
//                    System.out.println(databin);
                } else {
                    databin += "1";
                    ktra8bit0 = 0;
                    count++;
//                    System.out.println(databin);
                }
                data++;
                if (count == 8) {
                    if (ktra8bit0 == 8) {
//                        System.out.println("Du");
                        break;
                    }
                    ktra8bit0 = 0;
                    count = 0;
                }
            }
            databin = databin.substring(0, databin.length() - 8); // cat 8 bit 0 cuoi message
            System.out.println(databin);
            for (int i = 0; i < databin.length(); i = i + 8) {
                mess += bintomess(databin.substring(i, i + 8));
            }
            System.out.println("Message: " + mess);
        } catch (IIOException ex) {
            System.out.println("can't read file");
        }
    }
}
