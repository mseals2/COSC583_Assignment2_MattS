
import java.math.BigInteger;
import java.util.Random;
import java.util.regex.Pattern;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.*;
import java.io.*;

public class RSAkeyGen {

	public static void main(String[] args) throws Exception{

		String PubFile = "";
		String SecFile = "";
		String CertFile = "";
		String Certdata = "";
		int bitnum = 0;
		int bitlen = 0;
		BigInteger p;
		BigInteger q;
		BigInteger e;
		BigInteger d;
		BigInteger phi;
		MillerRabin MR = new MillerRabin();
		BigInteger ZERO = new BigInteger("0");
		BigInteger ONE = new BigInteger("1");
		BigInteger TWO = new BigInteger("2");
		BigInteger top = new BigInteger("20");
		Random r = new Random();

		for(int i = 0; i < args.length; i++){

			if(args[i].equals("-p")){
				PubFile = args[i+1];
			}

			if(args[i].equals("-s")){
				SecFile = args[i+1];
			}
			if(args[i].equals("-n")){
				bitnum = Integer.parseInt(args[i+1]);
			}
			if(args[i].equals("-c")){
				CertFile = args[i+1];
			}

		}

		Certdata = PubFile.split(".txt")[0] + "-casig.txt"; // strip .txt then add naming convention
		PrintWriter PubWriter = new PrintWriter(PubFile, "UTF-8");
		PrintWriter SecWriter = new PrintWriter(SecFile, "UTF-8");
		PrintWriter CertWriter = new PrintWriter(Certdata, "UTF-8");

		//generate the public key (n,e)
		bitlen = bitnum/2;
		p = MR.genPrime(bitlen);
		q = MR.genPrime(bitlen);
		e = MR.genPrime(bitlen / 2);
		BigInteger N = p.multiply(q);
		phi = p.subtract(ONE).multiply(q.subtract(ONE));
		e = MR.uniformRandom(ONE, top);
		//e = BigInteger.probablePrime(bitlen / 2, r);
		while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)
        {
            e = e.add(BigInteger.ONE);
        }
		//private key (d)
		d = e.modInverse(phi);

		if(CertFile != ""){ //sign with provided CA
			BigInteger C = new BigInteger(ReadFile(CertFile).get(0));
			BigInteger Hash_e = Hash(e);
			BigInteger Cert = MR.FastmodPow(Hash_e, C, N);
			CertWriter.println(Cert);
			CertWriter.println(N);
		}

		else{ //use your own private key as the CA otherwise
			BigInteger Hash_e = Hash(e);
			BigInteger Cert = MR.FastmodPow(Hash_e, d, N);
			CertWriter.println(Cert);
			CertWriter.println(N);
		}

		PubWriter.println(bitnum);
		PubWriter.println(N);
		PubWriter.println(e);

		SecWriter.println(bitnum);
		SecWriter.println(N);
		SecWriter.println(d);




		PubWriter.close();
		SecWriter.close();
		CertWriter.close();
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
}
