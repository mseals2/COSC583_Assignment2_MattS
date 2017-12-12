'''
cipher Block Chained encryption with AES cipher. 
AKA game of types: Python edition 

Matt Seals: last revised 20171006 


'''
from Crypto.Cipher import AES
import sys

def get_args():

        args = {}
        for i in range(len(sys.argv)):
                if(sys.argv[i] == '-k'):
                        args['Key'] = sys.argv[i + 1]

                if(sys.argv[i] == '-m'):
                        args['source'] = sys.argv[i + 1]

                if(sys.argv[i] == '-t'):
                        args['output'] = sys.argv[i + 1]
        return args




def read(name):
	f = open(name, 'rb')
	text = f.read()
	n = 16
	blocks = [text[i:i+n] for i in range(0, len(text), n)]
	return blocks

def pad(blocks):
	if(len(blocks[len(blocks)-1]) < 16 ):
		i = 16 - len(blocks[len(blocks)-1])
		p = i.to_bytes(1, sys.byteorder)
		padded_block = blocks[len(blocks)-1] + (p * i)
		blocks[len(blocks)-1] = padded_block
		blocks.append((p*16))

	else:
		i = 0
		p = i.to_bytes(1, sys.byteorder)
		blocks.append(p * 16)

	return blocks


def write(data, name):
	f = open(name, 'w')
	f.write(str(data))



def encrypt(blocks, IV, Key):
	Cipher_Text = []
	Cipher_Text.append(IV)
	Current_IV = IV
	cipher = AES.AESCipher(Key[:16], AES.MODE_ECB)

	for block in blocks: 
		txt = block
		r = int.from_bytes(txt, sys.byteorder) ^ int.from_bytes(Current_IV, sys.byteorder)
		C = cipher.encrypt(r.to_bytes(len(txt), sys.byteorder))
		Current_IV = C
		#Cipher_Text.append(C)
	
	return int.from_bytes(Current_IV, sys.byteorder)

if __name__ == '__main__':
	args = get_args()
	source_file = args['source']
	key_file = args['Key']
	output = args['output']
	#IV_file = args['IV']

	plain_text = read(source_file)
	IV = b"0000000000000000";
	key = read(key_file)[0]
	TX_blocks = pad(plain_text)
	C = encrypt(TX_blocks, IV, key)
	write(C, output)

if __name__ == '__main__':
	args = get_args()
	source_file = args['source']
	key_file = args['Key']
	output = args['output']
	#IV_file = args['IV']

	plain_text = read(source_file)
	IV = b"0000000000000000";
	key = read(key_file)[0]
	TX_blocks = pad(plain_text)
	C = encrypt(TX_blocks, IV, key)
	write(C, output)
