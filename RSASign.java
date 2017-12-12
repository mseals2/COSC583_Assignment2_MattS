
import java.util.Random;
import java.util.Arrays;
import java.util.*;
import java.math.BigInteger;
import java.io.*;
import java.security.MessageDigest;
import java.io.FileInputStream;
public class RSASign{

	static public void main(String[] args){

		MillerRabin MR = new MillerRabin();

		String KeyFile = "";
		String InFile = "";
		String OutFile = "";
		int N_len = 0;
		BigInteger m;
		BigInteger e;
		BigInteger N;
		BigInteger C;
		for(int i = 0; i < args.length; i++){

			if(args[i].equals("-k")){
				KeyFile = args[i+1];
			}
			if(args[i].equals("-m")){
				InFile = args[i+1];
			}
			if(args[i].equals("-s")){
				OutFile = args[i+1];
			}
		}
		byte[] msgBytes = HashFile(InFile);

		BigInteger MBI = new BigInteger(msgBytes).abs();

		List <String> KeyData = ReadFile(KeyFile);

		N = new BigInteger(KeyData.get(1));
		e = new BigInteger(KeyData.get(2));

		C = MR.FastmodPow(MBI, e, N);

		//C = MBI.modPow(e, N); // the built in function for comparison in debug
		//C = m^e mod n

		byte[] T = C.toByteArray();
		WriteFile(OutFile, C);


	}

	public static List <String> ReadFile(String inFile){

		List <String> fileData = new ArrayList<String>();
		String line;
		try{

			FileReader fileReader = new FileReader(inFile);
			BufferedReader buffreader = new BufferedReader(fileReader);
			while((line = buffreader.readLine()) != null){
				fileData.add(line);
			}
			fileReader.close();
		}catch(FileNotFoundException ex){

			System.out.println("Unable to open file" + inFile);
		}
		catch(IOException ex){

			System.out.println("error");
		}


		return fileData;
	}

	public static byte[] HashFile(String InFile){
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			FileInputStream fis = new FileInputStream(InFile);
			byte[] dataBytes = new  byte[1024];
			int nread = 0;
			while((nread = fis.read(dataBytes)) != -1){
				md.update(dataBytes,0,nread);
			}
			byte[] mdbytes = md.digest();
			return mdbytes;

	}
	catch( FileNotFoundException ex ){
		return null;
	}
	catch( IOException ex){
		return null;
	}
	catch(Exception E){
		return null;
	}
}

	public static void WriteFile(String outFile, BigInteger BI){
		try{
			File file = new File(outFile);
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.println(BI);
			writer.close();
		}catch(FileNotFoundException ex){
			System.out.println("Unable to open file" + outFile);
		}
		catch(IOException ex){
				System.out.println("error");
		}

	}


}
