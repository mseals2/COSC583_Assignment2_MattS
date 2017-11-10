import java.math.BigInteger;
import java.util.Random;
import java.util.regex.Pattern;

public class MillerRabin {

	private static final BigInteger ZERO = BigInteger.ZERO;
	private static final BigInteger ONE = BigInteger.ONE;
	private static final  BigInteger TWO = new BigInteger("2");
	private static final BigInteger THREE = new BigInteger("3");

	public static boolean isProbPrime(BigInteger n, int k) {

		if (n.compareTo(ONE) == 0)
			return false;
		if (n.compareTo(THREE) < 0)
			return true;
		int s = 0;
		BigInteger d = n.subtract(ONE);
		while (d.mod(TWO).equals(ZERO)) {
			s++;
			d = d.divide(TWO);
		}
		for (int i = 0; i < k; i++) {
			BigInteger a = uniformRandom(TWO, n.subtract(ONE));
			BigInteger x = FastmodPow(a, d, n);

			if (x.equals(ONE) || x.equals(n.subtract(ONE)))
				continue;
			int r = 0;
			for (; r < s; r++) {
				x = FastmodPow(x, TWO, n);
				if (x.equals(ONE))
					return false;
				if (x.equals(n.subtract(ONE)))
					break;
			}
			if (r == s) // None of the steps made x equal n-1.
				return false;
		}
		return true;
	}

	public static BigInteger uniformRandom(BigInteger bottom, BigInteger top) {
		Random rnd = new Random();
		BigInteger res;
		do {
			res = new BigInteger(top.bitLength(), rnd);
		} while (res.compareTo(bottom) < 0 || res.compareTo(top) > 0);
		return res;
	}


	public static BigInteger FastmodPow(BigInteger base, BigInteger exponent, final BigInteger modulo) {

                BigInteger result = BigInteger.ONE;
                while (exponent.compareTo(BigInteger.ZERO) > 0) {
                        if (exponent.testBit(0)) // then exponent is odd
                                result = (result.multiply(base)).mod(modulo);
                        exponent = exponent.shiftRight(1);
                        base = (base.multiply(base)).mod(modulo);
                }
                return result.mod(modulo);

               }

	public static BigInteger genPrime(int n){

		BigInteger BI;
		String IS;
                do{
                        String binString = "";
                        for(int i = 0; i < n; i++){

                                Random rg = new Random();
                                int b = rg.nextInt(2);
                                binString+=Integer.toBinaryString(b);
                        }

			//IS = toIntString(binString);
                      	BI = new BigInteger(binString, 2);
                }while(!isProbPrime(BI, 40));

                return BI;

	}

	static String toIntString(String s){

		int interval = 4;
		int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
    		String intString = "";
		String[] result = new String[arrayLength];

    		int j = 0;
    		int lastIndex = result.length - 1;
    		for (int i = 0; i < lastIndex; i++) {
        		result[i] = s.substring(j, j + interval);
        		j += interval;
    		} //Add the last bit
    		result[lastIndex] = s.substring(j);
		for(int i = 0; i < result.length; i++){
			intString+= Integer.parseInt(result[i],2);
		}
    		return intString;

	}
}
