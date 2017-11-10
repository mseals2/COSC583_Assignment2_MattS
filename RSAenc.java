
import java.util.Random;
import java.util.Arrays;
import java.util.*;
import java.math.BigInteger;
import java.io.*;
public class RSAenc{

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
			if(args[i].equals("-i")){
				InFile = args[i+1];
			}
			if(args[i].equals("-o")){
				OutFile = args[i+1];
			}
		}

		List <String> Messagedata = ReadFile(InFile);
		List <String> KeyData = ReadFile(KeyFile);

		m = new BigInteger(Messagedata.get(0));
		N_len = Integer.parseInt(KeyData.get(0));
		N = new BigInteger(KeyData.get(1));
		e = new BigInteger(KeyData.get(2));

		byte[] pad = getPad(m.bitLength(), N_len);
		byte[] marry = m.toByteArray();
		if (marry[0] == 0) {
    	byte[] tmp = new byte[marry.length - 1];
    	System.arraycopy(marry, 1, tmp, 0, tmp.length);
    marry = tmp;
		}

		byte[] M = new byte[marry.length + pad.length];
		System.arraycopy(pad, 0, M, 0, pad.length);
		System.arraycopy(marry, 0, M, pad.length, marry.length);
		BigInteger MBI = new BigInteger(M);

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

	public static void WriteFile(String outFile, BigInteger BI){
		try{
			PrintWriter writer = new PrintWriter(outFile, "UTF-8");
			writer.println(BI);
			writer.close();
		}catch(FileNotFoundException ex){
			System.out.println("Unable to open file" + outFile);
		}
		catch(IOException ex){
				System.out.println("error");
		}


	}

	public static byte[] getPad(int mlen, int nlen){

		Random rand = new Random();
		Boolean illegalChar = Boolean.FALSE;
		int rlen = (nlen/2)/8;
		int n;
		byte[] r_core = new byte[rlen-3];
		byte[] r = new byte[rlen];

		do{
				rand.nextBytes(r_core);
				illegalChar = Boolean.FALSE;
				for(int i=0; i < r_core.length; i++){
					if(r_core[i] == 0x00){illegalChar = Boolean.TRUE;}
				}
			}while(illegalChar == Boolean.TRUE);

		r[0] = 0x00;
		r[1] = 0x02;
		for(int i=0; i < r_core.length; i++){
			r[i+2] = r_core[i];
		}
		r[r.length-1] = 0x00;
		return r;
	}


}
