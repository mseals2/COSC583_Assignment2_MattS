import java.lang.Runtime;
import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class unlock{

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

    // System.out.println(directory);
    // System.out.println(actionPublicKey);
    // System.out.println(actionPrivateKey);
    // System.out.println(validatingPubKey);
    List <String> Pubkeydata = ReadFile(actionPublicKey);
    List <String> ValidatingData = ReadFile(validatingPubKey);
    List <String> PrivKeyData = ReadFile(actionPrivateKey);
    List <String> SymmetricKeyData = ReadFile(directory + "/SymmetricKeyManifest.txt");

    BigInteger VK = new BigInteger(ValidatingData.get(0));
    BigInteger PubKey = new BigInteger(Pubkeydata.get(2));
    BigInteger N_Pub = new BigInteger(Pubkeydata.get(1));

    BigInteger N_Priv = new BigInteger(PrivKeyData.get(1));
    BigInteger PrivKey = new BigInteger(PrivKeyData.get(2));

    BigInteger C_SymmetricKey = new BigInteger(SymmetricKeyData.get(0));
    BigInteger C_SymmetricKey_Signed = new BigInteger(SymmetricKeyData.get(1));

    //verify the public key received with certfile
    if(! VerifyPublicKey(PubKey, N_Pub, VK)){
      System.out.println("PublicKey doesnt checkout..");
      return;

    }
    else{
      //System.out.println("PublicKey checks out!");
    }


    //verify the Symmetric Key manifest with the public key
    if(RSAVerify(actionPublicKey, C_SymmetricKey.toString(), C_SymmetricKey_Signed.toString()) == "False"){
        System.out.println("SymmetricKey doesnt checkout..");
        return;
    }
    else{
      //System.out.println("SymmetricKey checkout!");
    }

    //retreive the SymmtricKey
    BigInteger SymmetricKey = RSAdec(actionPrivateKey, C_SymmetricKey.toString());

    DecryptDirectory(directory, SymmetricKey);

    CleanUp(directory);

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

  public static String RSAVerify(String keyFile, String message, String Signature){
    List<String> args = new ArrayList<String>();
    //System.out.println("RSAVerify");
    try{
    PrintWriter msgWriter = new PrintWriter("Infile.txt", "UTF-8");
    PrintWriter SigWriter = new PrintWriter("Signature.txt");
    msgWriter.println(message);
    SigWriter.println(Signature);
    SigWriter.close();
    msgWriter.close();
    }
    catch(Exception ex){return null;}

    String call = "java RSAValidate" + " -k " + keyFile + " -m Infile.txt" + " -s Signature.txt";    
    //System.out.println(call);
    try{
      Process p = Runtime.getRuntime().exec(call);
      p.waitFor();
      args = ReadProcess(p);
    }
    catch(Exception ex){System.out.println("Wait Exception");}
    //BigInteger BI = new BigInteger(ReadFile("Signature.txt").get(0));
    //System.out.println(BI);
    //System.out.println(args.get(0));
    return args.get(0);
  }

  public static List<String> ReadProcess(Process p){
    try{
      List<String> Output = new ArrayList<String>();
      InputStream stdin = p.getInputStream();
      InputStreamReader isr = new InputStreamReader(stdin);
      BufferedReader br = new BufferedReader(isr);

      String line = null;

      while ( (line = br.readLine()) != null){
           Output.add(line);
           //System.out.println(line);
        }
        return Output;
    }
    catch(Exception ex){
      return null;
    }
    
}
  public static BigInteger RSAdec(String keyFile, String CipherText){
    
    try{
    PrintWriter msgWriter = new PrintWriter("CipherText.txt", "UTF-8");
    msgWriter.println(CipherText);;
    msgWriter.close();
    }
    catch(Exception ex){return null;}

    String call = "java RSAdec" + " -k " + keyFile + " -i CipherText.txt" + " -o Outfile.txt";    
    //System.out.println(call);
    try{
      Process p = Runtime.getRuntime().exec(call);
      p.waitFor();
    }
    catch(Exception ex){System.out.println("Wait Exception");}
    BigInteger BI = new BigInteger(ReadFile("Outfile.txt").get(0));
    //System.out.println(BI);
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

public static boolean DecryptDirectory(String directory, BigInteger key){

  File OrigianlDirecotry = new File(directory.split("-")[0]);
  String ODirectory = OrigianlDirecotry.getName();
  if (!OrigianlDirecotry.exists()) {
      //System.out.println("creating directory: " + OrigianlDirecotry.getName());
      boolean result = false;

      try{
          OrigianlDirecotry.mkdir();
          result = true;
      }
      catch(SecurityException se){
          //handle it
      }
      if(result) {
          //System.out.println("DIR created");
      }
    }

  try{
    File SKFile = new File("SymmetricKey.txt");
    PrintWriter SKFileWriter = new PrintWriter(SKFile, "UTF-8");
    SKFileWriter.println(key);
    SKFileWriter.close();

    Set<String> Fileset = new HashSet<String>();
    File[] files = new File(directory).listFiles();
    for(File file: files){
      String[] name = file.getName().split("-");
      if(name.length > 1){
        Fileset.add(name[0]);
      }
    }

  if(!verifyTags(Fileset, key, directory)){
    return false;
    }

  //System.out.println("FilesCheckOut");

  for(String file: Fileset){
    String input = directory + "/" + file + "-enc.txt";
    String OutPut = ODirectory + "/" + file;
    String call = "python3 cbc-dec.py -k " + "SymmetricKey.txt" + " -i " + input + " -o " + OutPut;
      
    //System.out.println(call);
    Process p = Runtime.getRuntime().exec(call);
    p.waitFor();

  }
  }
  catch(Exception ex){
    return false;
  }

  return true;
}
public static boolean verifyTags(Set<String> Fileset, BigInteger key, String directory){
  String line;
  //System.out.println(Fileset);
  try{
  for(String file: Fileset){
    String message = directory + file + "-enc.txt";
    String tag = directory + file + "-enc-tag.txt";
    String call = "python3 cbcmac-validate.py -k " + "SymmetricKey.txt" + " -m " + message + " -t " + tag;
    //System.out.println(call);
    Process p = Runtime.getRuntime().exec(call);
    p.waitFor();
    BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
    while(( line = bri.readLine()) != null){
      if(line == "FALSE"){
        return false;
      }
      //System.out.println(line);
    }
  }
  return true;
  }
  catch(Exception ex){
    return false;
  }
}


public static void CleanUp(String OriginalDir){
  try{
   File[] originalDir_files = new File(OriginalDir).listFiles();

   File OGDir = new File(OriginalDir);
   File infile = new File("Infile.txt");
   File SigFile = new File("Signature.txt");
   File OutFile = new File("Outfile.txt");
   File SymFile = new File("SymmetricKey.txt");
   File CFile = new File("CipherText.txt");
   for(File file : originalDir_files){
      file.delete();
    }

    infile.delete();
    SigFile.delete();
    OutFile.delete();
    SymFile.delete();
    OGDir.delete();
    CFile.delete();
   } 
   catch(Exception ex){
    System.out.println("Exception thrown in CleanUp");
   }
  
  }
}
