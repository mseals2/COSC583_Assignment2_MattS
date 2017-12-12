/*
 * NOT DONE.. Clean up code and activate file cleanup
 *
 *
 */


import java.lang.Runtime;
import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class lock{

  static public void main(String args[]){

    String directory = null;
    String actionPublicKey = null;
    String actionPrivateKey = null;
    String validatingPubKey = null;

    for(int i=0; i < args.length; i++){

    if(args[i].equals("-d")){
      directory = args[i+1];
    }
    if(args[i].equals("-p")){
      actionPublicKey = args[i+1];
    }
    if(args[i].equals("-r")){
      actionPrivateKey = args[i+1];
    }
    if(args[i].equals("-vk"))
      validatingPubKey = args[i+1];
    }
    //System.out.println(actionPublicKey);
    //System.out.println(validatingPubKey);

    List <String> actionPublic = ReadFile(actionPublicKey);
    List <String> ValidatingData = ReadFile(validatingPubKey);
    List <String> actionPrivate = ReadFile(actionPublicKey);

    BigInteger VK = new BigInteger(ValidatingData.get(0));
    BigInteger PubKey = new BigInteger(actionPublic.get(2));
    BigInteger N_Pub = new BigInteger(actionPublic.get(1));

    BigInteger N_Priv = new BigInteger(actionPrivate.get(1));
    BigInteger PrivKey = new BigInteger(actionPrivate.get(2));

    if(! VerifyPublicKey(PubKey, N_Pub, VK)){
      System.out.println("PublicKey doesnt checkout..");
    }
    else{
      //System.out.println("PublicKey checks out!");
    }

    BigInteger SymmetricKey = keygen();
    

    EncryptDirectory(directory, SymmetricKey);
    TagDirectory(directory + "-lock", SymmetricKey.toString());

    BigInteger C_SymmetricKey = RSAenc(actionPublicKey);

    BigInteger C_SymmetricKey_Signed = RSASign(actionPrivateKey, C_SymmetricKey.toString());

    try{
      File SMFfile = new File( directory + "-lock/" + "SymmetricKeyManifest.txt");
      PrintWriter SKMWriter = new PrintWriter(SMFfile, "UTF-8");
      SKMWriter.println(C_SymmetricKey);
      SKMWriter.println(C_SymmetricKey_Signed);
      SKMWriter.close();
    }
    catch(Exception ex){
      System.out.println("Symmetric key manifest write Exception thrown");
    }

    CleanUp(directory);

  }
  public static void TagDirectory(String directoryName, String key){
    //System.out.println("Tagging");
    //System.out.println(directoryName);
    File[] files = new File(directoryName).listFiles();
    String KeyFile = "SymmetricKey.txt";

    for(File file: files){
      //System.out.println(file.getName());
      try{
        String infile = directoryName + '/' + file.getName();
        String Outfile = directoryName + '/' + file.getName().split(".txt")[0] + "-tag.txt";
        String call = "python3 cbcmac-tag.py " + " -m " + infile + " -t " + Outfile + " -k " + KeyFile;
        //System.out.println(call);
        Process p = Runtime.getRuntime().exec(call);

      }
      catch(Exception ex){
        System.out.println("Exception occured");
      }
    }

  }

  public static boolean VerifyPublicKey(BigInteger PubKey, BigInteger N, BigInteger VK){

    BigInteger A = Hash(PubKey).mod(N);
    BigInteger B = VK.modPow(PubKey,N);
    if(A.compareTo(B) == 0){
      return true;
    }
    else{
      return false;
    }

  }
  public static BigInteger keygen(){

    byte[] b = new byte[16];
    new Random().nextBytes(b);
    BigInteger BI = new BigInteger(b);
    //System.out.println("Symmetric Key is: " + BI.toString());
    return BI.abs();

  }

  public static BigInteger RSASign(String KeyFile, String message){
    try{
    PrintWriter msgWriter = new PrintWriter("Infile.txt", "UTF-8");
    msgWriter.println(message);
    msgWriter.close();
    }
    catch(Exception ex){return null;}

    String call = "java RSASign" + " -k " + KeyFile + " -m Infile.txt" + " -s Signature.txt";    
    //System.out.println(call);
    try{
      Process p = Runtime.getRuntime().exec(call);
      p.waitFor();
    }
    catch(Exception ex){System.out.println("Wait Exception");}
    BigInteger BI = new BigInteger(ReadFile("Signature.txt").get(0));
    //System.out.println(BI);
    return BI;
    

  }

  public static BigInteger RSAenc(String KeyFile){
    String call = "java RSAenc" + " -k " + KeyFile + " -i SymmetricKey.txt" + " -o Outfile.txt";    
    //System.out.println(call);
    try{
      Process p = Runtime.getRuntime().exec(call);
      p.waitFor();
    }
    catch(Exception ex){System.out.println("Wait Exception");}
    BigInteger BI = new BigInteger(ReadFile("Outfile.txt").get(0));
    //System.out.println("the key encrypted Key is: " + BI.toString());
    return BI;
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



public static void EncryptDirectory(String directoryName, BigInteger key){

  File lockedDir = new File(directoryName + "-lock");
  File originalDir = new File(directoryName);

  if (!lockedDir.exists()) {
      //System.out.println("creating directory: " + lockedDir.getName());
      boolean result = false;

      try{
          lockedDir.mkdir();
          result = true;
      }
      catch(SecurityException se){
          //handle it
      }
      if(result) {
          //System.out.println("DIR created");
      }
    }

  String IV = keygen().toString();

  WriteCBCData(key.toString(), IV);

  String IVFile = "CBC-IV.txt";
  String KeyFile = "SymmetricKey.txt";

  File[] files = new File(directoryName).listFiles();
  for(File file: files){
    try{

      String infile = directoryName + '/' + file.getName();
      String Outfile = directoryName + "-lock" + '/' + file.getName().split(".txt")[0] + "-enc.txt";
      String call = "python3 cbc-enc.py" + " -i " + infile + " -o " + Outfile + " -k " + KeyFile + " -v " + IVFile;
      //System.out.println(call);
      Process p = Runtime.getRuntime().exec(call);
      p.waitFor();
    }
    catch(Exception ex){
      System.out.println("Exception occured");
    }
     
  }

}

public static void WriteCBCData(String Key, String IV){
  try {
    String IVFile = "CBC-IV.txt";
    String KeyFile = "SymmetricKey.txt";
    PrintWriter IVWriter = new PrintWriter(IVFile, "UTF-8");
    PrintWriter KeyWriter = new PrintWriter(KeyFile, "UTF-8");

    IVWriter.println(IV);
    KeyWriter.println(Key);
    //System.out.println("Symmetric Key written is: " + Key);
    IVWriter.close();
    KeyWriter.close();
  }
  catch(Exception ex){
    System.out.println("Exception occured at WriteCBCData");
  }

}

public static BigInteger Hash(BigInteger B){
  try{
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(B.toByteArray());
    BigInteger Hash = new BigInteger(hash);
    return Hash;
  }
catch(Exception ex){
    System.out.println("Hash function throws Exception");
    return null;
  }
}


public static void CleanUp(String OriginalDir){
  try{
    File[] originalDir_files = new File(OriginalDir).listFiles();

    File OGDir = new File(OriginalDir);
    File infile = new File("Infile.txt");
    File OutFile = new File("OutFile.txt");
    File SymFile = new File("SymmetricKey.txt");
    File SigFile = new File("Signature.txt");
    File IVFile = new File("CBC-IV.txt");

    for(File file: originalDir_files){
      file.delete();
    }
    OGDir.delete();
    infile.delete();
    OutFile.delete();
    SymFile.delete();
    SigFile.delete();
    IVFile.delete();
  }
  catch(Exception ex){
    System.out.println("Exception occured in CleanUp");
    }

  }
}
