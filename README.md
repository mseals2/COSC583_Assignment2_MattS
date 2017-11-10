# COSC583_Assignment2_MattS
Assignment for COSC583 -- applied cryptography -- by Matt Seals)
for anyone that comes along that is interested in this code

contains:
  // classes that preform RSA PCKS#1 v1.5 i.e. padded rsa (convention for pad is 0x02||r||0x00||m )
  - RSAkeyGen.java -- generates two files containing public and private keys
  - RSAenc.java -- generates a cipher text file using previously generated public key file
  - RSAdec.java -- generates a decrypted file using private key file and cipher text file

  // implements the above classes by wrapping them in a name that is accepted by auto-grade script//
  // since java will not allow for hyphens in .java names as a convention // 

  - rsa-keygen.sh
  - rsa-enc.sh
  - rsa-dec.sh
 usage: 
 
  sudo chmod +x <name>.sh //
  rsa-keygen.sh -p <public key file> -s <secret key file> -n <number of bits>
  rsa-enc.sh -k <key file public> -i <input file> -o <output file>
  rsa-dec.sh -k <key file secret> -i <input file (cipher text)> -o <output file>
  
