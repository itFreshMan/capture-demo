package cn.edu.ahpu.capture.demo.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class OcrImageUtils {
	
	////根据大批量的观察验证码，寻找规律
	private static int leftMargin = 0,rightMargin = 0,topMargin = 0,bottomMargin = 0;
	
	public static void main(String[] args) throws Exception {
		File f = new File("yzm\\a_original\\");
		File[] images = f.listFiles();
		File img = null;
		
	/*	int length = images.length;
		double randomDouble = Math.random()*(length-1);
		int randomInt = Integer.parseInt(new DecimalFormat("0").format(randomDouble));
		img = images[randomInt];
	*/	
		
		img = new File("yzm\\a_original\\20150325110707.png");
		
		try {
			System.out.println(img.getName()+" ---------> "+ getCaptcha(img.getName(),5,5,0,4));
		}  catch (java.lang.ArrayIndexOutOfBoundsException e) {
			System.err.println("java.lang.ArrayIndexOutOfBoundsException:"+img.getName());
			e.printStackTrace();
		}
		
	
	/*	for(File imgEach : images){
			try {
				System.out.println(imgEach.getName()+" ---------> "+ getCaptcha(imgEach.getName(),5,5,0,4));
			}  catch (java.lang.ArrayIndexOutOfBoundsException e) {
				System.err.println("java.lang.ArrayIndexOutOfBoundsException:"+imgEach.getName());
				e.printStackTrace();
			}
		}*/
			
	}
	
	/**
	 * 根据验证码图片,识别验证码
	 * @param fileName
	 * @return
	 */
	public static String getCaptcha(String fileName,int x1 ,int x2 , int y1 ,int y2) throws Exception{
		String result = "";
		 leftMargin = x1; //左边距
		 rightMargin = x2 ;//
		 topMargin = y1 ;
		 bottomMargin = y2;
		
		BufferedImage img = removeBackgroud("yzm\\a_original\\"+fileName);//去除背景色;
		
		File removebgFile = new File("yzm\\b_removebg\\"+fileName);
		ImageIO.write(img, "PNG", removebgFile);
		
		List<BufferedImage> listImg = splitImage(img,fileName);//分割，去除空白;
		Map<BufferedImage, String> map = loadTrainData();
		
		for (BufferedImage bi : listImg) {
			result += getSingleCharOcr(bi, map);
		}
		ImageIO.write(img, "PNG", new File("yzm\\d_result\\"+result+".png"));
		
		removebgFile.deleteOnExit();
		return result;
	}
	
	public static BufferedImage removeBackgroud(String picFile)
			throws Exception {
		BufferedImage img = ImageIO.read(new File(picFile));
		int width = img.getWidth();
		int height = img.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (isWhite(img.getRGB(x, y)) == 1) {
					img.setRGB(x, y, Color.WHITE.getRGB());
				} else {
					img.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
//		return img;
		//验证码margin 给裁剪掉;
		return img.getSubimage(leftMargin, topMargin, width - leftMargin - rightMargin, height - topMargin - bottomMargin);  
	}

	//纵向切割图片,
	public static List<BufferedImage> splitImage(BufferedImage img,String fileName)
			throws Exception {
		List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		int width = img.getWidth();
		int height = img.getHeight();
		List<Integer> weightlist = new ArrayList<Integer>();
		
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
//				if (isWhite(img.getRGB(x, y)) == 1) {//验证码为：白色字体，黑色底纹;
				if (isBlack(img.getRGB(x, y)) == 1){//验证码为：黑色字体，白色底纹;
					count++;
				}
			}
			weightlist.add(count);
		}
		
		for (int i = 0; i < weightlist.size();) {
			int length = 0; //验证码 连续的宽度;
			while (weightlist.get(i++) > 1) {
				length++;
				
				if(i >= weightlist.size() ){
					break;
				}
			}
			
		/*	if (length > 12) {
				subImgs.add(removeBlank(img.getSubimage(i - length - 1, 0,
						length / 2, height)));
				subImgs.add(removeBlank(img.getSubimage(i - length / 2 - 1, 0,
						length / 2, height)));
			} else if (length > 3) {
				subImgs.add(removeBlank(img.getSubimage(i - length - 1, 0,
						length, height)));
			}*/
			
			//验证码的每个字符宽度,1最短，7最宽，最宽不超过10，最短不少于 3
			if (length > 3 && length < 10) {
				subImgs.add(removeBlank(img.getSubimage(i - length - 1, 0,length, height)));
			}
		}
		
		//训练阶段,可以通过执行下面一段代码，寻找最好的分割后的 单个字符的模版
		/*
		if(subImgs != null && subImgs.size() > 0){
			File dir = new File("yzm\\c_split\\"+fileName.substring(0,fileName.lastIndexOf(".")));
			if(!dir.exists()){
				dir.mkdirs();
			}
			for(int k = 0 ; k <subImgs.size() ; k++){
				ImageIO.write(subImgs.get(k), "PNG", new File(dir+"\\"+k+".png"));
			}
		}*/
		return subImgs;
	}
	
	//横向切割:给纵向切割后的每个字符,去除 上,下边距空白
	public static BufferedImage removeBlank(BufferedImage img) throws Exception {
		int width = img.getWidth();
		int height = img.getHeight();
		int start = 0;
		int end = 0;
		Label1: for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				if (isBlack(img.getRGB(x, y)) == 1) {
					start = y;
					break Label1;
				}
			}
		}
		Label2: for (int y = height - 1; y >= 0; --y) {
			for (int x = 0; x < width; ++x) {
				if (isBlack(img.getRGB(x, y)) == 1) {
					end = y;
					break Label2;
				}
			}
		}
		return img.getSubimage(0, start, width, end - start + 1);
	}

	public static Map<BufferedImage, String> loadTrainData() throws Exception {
		Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
		File dir = new File("yzm\\train\\train3");
		File[] files = dir.listFiles();
		for (File file : files) {
			map.put(ImageIO.read(file), file.getName().charAt(0) + "");
		}
		return map;
	}	
	
	public static String getSingleCharOcr(BufferedImage img,
			Map<BufferedImage, String> map) {
		String result = "";
		int width = img.getWidth();
		int height = img.getHeight();
		int min = width * height;
		for (BufferedImage bi : map.keySet()) {
			int count = 0;
			Label1: for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if(x >= width || y >= height || x >= bi.getWidth() || y >= bi.getHeight()){
						break Label1;
					}
					if (isWhite(img.getRGB(x, y)) != isWhite(bi.getRGB(x, y))) {
						count++;
						if (count >= min)
							break Label1;
					}
				}
			}
			if (count < min) {
				min = count;
				result = map.get(bi);
			}
		}
		return result;
	}	
	
	//rgb <= 358 黑色
	public static int isBlack(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() <= 348) {//350,400
			return 1;
		}
		return 0;
	}    
	
	//rgb >358 白色
	public static int isWhite(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() > 348) {
			return 1;
		}
		return 0;
	}
}
