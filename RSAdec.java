import java.util.Random;
import java.util.Arrays;
import java.util.*;
import java.math.BigInteger;
import java.io.*;

public class RSAdec{

  public static void main(String[] args){

    String KeyFile = "";
    String InFile = "";
    String OutFile = "";
    int N_len = 0;
    BigInteger d;
    BigInteger N;
    BigInteger C;
    BigInteger M;

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

		C = new BigInteger(Messagedata.get(0));
		N_len = Integer.parseInt(KeyData.get(0));
		N = new BigInteger(KeyData.get(1));
		d = new BigInteger(KeyData.get(2));

		M = Decrypt(C,d,N);
    WriteFile(OutFile, M);
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
  public static BigInteger Decrypt(BigInteger C, BigInteger d, BigInteger N){
    MillerRabin MR = new MillerRabin();

    BigInteger M_prime;
    M_prime = MR.FastmodPow(C, d, N);
    //M_prime = C.modPow(d, N); //BigInteger's built in
    byte[] m_r = M_prime.toByteArray();
		if (m_r[0] == 0) {
    	byte[] tmp = new byte[m_r.length - 1];
    	System.arraycopy(m_r, 1, tmp, 0, tmp.length);
    m_r = tmp;
		}

    int index = 0;

    if(m_r[0] == 0x02 || m_r[1] == 0x02){
      for(int i = 0; i < m_r.length; i++){
          if(m_r[i] == 0x00 && i != 0){
            index = i;
          }
      }
    }
    byte[] m = new byte[m_r.length - (1+index)];
    System.arraycopy(m_r, index+1, m, 0, m.length);
    BigInteger M = new BigInteger(m);
    return M;
  }
}
