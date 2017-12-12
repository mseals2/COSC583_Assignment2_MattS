import java.util.Random;
import java.util.Arrays;
import java.util.*;
import java.math.BigInteger;
import java.io.*;
import java.security.MessageDigest;
import java.io.FileInputStream;

public class RSAValidate{

  public static void main(String[] args){

  MillerRabin MR = new MillerRabin();

    String KeyFile = "";
    String InFile = "";
    String Signature = "";
    int N_len = 0;
    BigInteger d;
    BigInteger N;
    BigInteger C;
    BigInteger M;

		for(int i = 0; i < args.length; i++){

			if(args[i].equals("-k")){
				KeyFile = args[i+1];
			}
			if(args[i].equals("-m")){
				InFile = args[i+1];
			}
			if(args[i].equals("-s")){
				Signature = args[i+1];
			}
		}

    byte[] msgBytes = HashFile(InFile);


		List <String> SignatureData = ReadFile(Signature);
    List <String> KeyData = ReadFile(KeyFile);

    BigInteger Sig = new BigInteger(SignatureData.get(0));
		N_len = Integer.parseInt(KeyData.get(0));
		N = new BigInteger(KeyData.get(1));
		d = new BigInteger(KeyData.get(2));

    BigInteger MBI = new BigInteger(msgBytes).abs();


    BigInteger MMOD  = MBI.mod(N);

    M = MR.FastmodPow(Sig, d, N);
    if(M.compareTo(MMOD) == 0){
      System.out.println("TRUE");
    }
    else{
      System.out.println("FALSE");
    }
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
}
