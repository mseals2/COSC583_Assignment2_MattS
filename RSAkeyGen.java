
import java.math.BigInteger;
import java.util.Random;
import java.util.regex.Pattern;
import java.io.PrintWriter;

public class RSAkeyGen {

	public static void main(String[] args) throws Exception{

		String PubFile = "";
		String SecFile = "";
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

		}
		PrintWriter PubWriter = new PrintWriter(PubFile, "UTF-8");
		PrintWriter SecWriter = new PrintWriter(SecFile, "UTF-8");

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

		PubWriter.println(bitnum);
		PubWriter.println(N);
		PubWriter.println(e);

		SecWriter.println(bitnum);
		SecWriter.println(N);
		SecWriter.println(d);

		PubWriter.close();
		SecWriter.close();
	}
}
