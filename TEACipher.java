package encryption;

import java.math.BigInteger;
//MOST RECENT FILE AS OF JANUARY 20th, 2017
public class TEACipher {

  private final int key[] = new int[4]; //16 byte key
  private final static int DELTA = 0x9E3779B9;
  String encoded;
  private byte[] keyInBytes = new BigInteger("599da89dbc7a8cb9099fd6858f8e39", 16).toByteArray();
  //private String encoded;
  //private byte[] keyB;
  
 

 
  public TEACipher(){
    for(int i= 0, j = 0;i<4; i++ ){
      
      key[i] = ((keyInBytes[j] & 0xff)) |
          ((keyInBytes[j++] & 0xff) <<  8) |
          ((keyInBytes[j++] & 0xff) << 16) |
          ((keyInBytes[j++] & 0xff) << 24);
    }
  }



  public void convertToInt(byte[] original, int[] converted, int offset){
    int shiftAmount = 24;
    int offsetAmount = offset;
    converted[offsetAmount] = 0;
    System.out.format("converted.length=%d\n", converted.length);
    for(int i=0; i < original.length; i++) {
      converted[offsetAmount] |= ((original[i] & 0xff) << shiftAmount);
      if (shiftAmount==0) {
        shiftAmount = 24;
        offsetAmount++;
        if (offsetAmount<converted.length){
          converted[offsetAmount] = 0;
        }
      }
      else {
        shiftAmount -= 8;
      }
    }
  }

  public byte[] convertToByte(int[] original,int offset, int decodeLength) {
    byte[] byteConvert = new byte[decodeLength];
    System.out.println(original.length);
    int offsetAmount = offset;

    int count = 0;
    for (int j = 0; j < decodeLength; j++) {
      byteConvert[j] =  (byte) ((original[offsetAmount] >>> (24 - (8*count))) & 0xff);
      count++;
      if (count == 4 && offsetAmount + 1 < original.length) {
        count = 0;
        offsetAmount++;
      }
    }
    return byteConvert;
  }

  public byte[] alvin_encode(byte[] source){
    int padding = source.length/8;
    if((source.length%8)==0){
      padding = padding+0;
    }
    else{
      padding +=1;
    }
    padding = padding *2;
    //FIXME: Bug7
    //int[] IntConvert = new int[padding + 1];
    //IntConvert[0] = source.length;
      int[] IntConvert = new int[padding];

      //FIXME: Bug8
    //convertToInt(source, IntConvert, 1);
    convertToInt(source, IntConvert, 0);
    System.out.format("alvin_encode: source=%d, IntConvert=\n", source.length, IntConvert.length);
    alvin_encrypt(IntConvert);
    return convertToByte(IntConvert,0,IntConvert.length*4);
  }

  public void alvin_encrypt (int[] encryptInt){
    int v0;
    int v1;
    //PREVIOUS (ALSO FIX #1): for( int i =1; i< encryptInt.length; i+=2){
    for(int i =0; i< encryptInt.length; i+=2){
      //int rotAmount = 32;
      v0 = encryptInt[i];
      v1 = encryptInt[i+1];
      int sum = 0;

      //PREVIOUS (FIX #2): int rotAmount = 32; rotAmount>= 0; rotAmount --
      for(int rotAmount = 31; rotAmount>= 0; rotAmount --){
        sum += DELTA;
        //PREVIOUS(Fix#3):
        //v0  += ((v1 << 4 ) + key[0] ^ v1) + (sum ^ (v1 >>> 5)) + key[1];
        //v1  += ((v0 << 4 ) + key[2] ^ v0) + (sum ^ (v0 >>> 5)) + key[3];

      //FIXME: New Bug1  
        //v1  += ((v0 << 4 ) + key[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + key[3]);
      //  v0  += ((v1 << 4 ) + key[0]) ^ (v1+  sum) ^ ((v1 >>> 5) + key[1]);
        
        
        v0  += ((v1 << 4 ) + key[0]) ^ (v1+  sum) ^ ((v1 >>> 5) + key[1]);  
        v1  += ((v0 << 4 ) + key[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + key[3]);
      }
      encryptInt[i] = v0;
      encryptInt[i+1] = v1;
    }
  }

  public byte[] alvin_decode(byte[] source) {
    int[] intConvert = new int[source.length / 4];

    convertToInt(source, intConvert, 0);

    System.out.format("intConvert:\n");
    for(int i=0; i<intConvert.length; i++){
      System.out.format("%h", intConvert[i]);
      if((i+1)%8==0){
        System.out.format("\n");
      }
    }
    System.out.format("\n");

    alvin_decrypt(intConvert);
    System.out.format("Decrypt Int:\n");
    for(int i=0; i<intConvert.length; i++){
      System.out.format("%h", intConvert[i]);
      if((i+1)%8==0){
        System.out.format("\n");
      }
    }
    //FIXME: bug3 : return convertToByte(intConvert, 1, 100);
    //FIXME: bug3a return convertToByte(intConvert, 1, 104);
    //FIXME: bug3b return convertToByte(intConvert, 0, 104);
    return convertToByte(intConvert, 0, source.length);
  }

  public void alvin_decrypt(int[] encodeInt){
    int v0, v1, sum, n;
    //FIXME: Bug 1: for(int i =1; i<encodeInt.length; i+=2) {
    for(int i =0; i<encodeInt.length; i+=2) {
      v0 = encodeInt[i];
      v1 = encodeInt[i+1];
      sum = 0xC6EF3720;
      System.out.format("%h", v0);
      System.out.format("%h", v1);
      System.out.format("---");


      //FIXME: bug 2: for (int rotAmount = 32; rotAmount >=0; rotAmount --) {
      for (int rotAmount = 31; rotAmount >=0; rotAmount --) {
        //v1  -= ((v0 << 4 ) + key[2] ^ v0) + (sum ^ (v0 >>> 5)) + key[3];
        //v0  -= ((v1 << 4 ) + key[0] ^ v1) + (sum ^ (v1 >>> 5)) + key[1];
      // FIXME: Bug4
        v1  -= ((v0 << 4 ) + key[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + key[3]);
        v0  -= ((v1 << 4 ) + key[0]) ^ (v1+  sum) ^ ((v1 >>> 5) + key[1]);

        sum -= DELTA;

        //d1 -= ((d0<<4) + k2) ^ (d0 + sum) ^ ((d0>>>5) + k3);
        //d0 -= ((d1<<4) + k0) ^ (d1 + sum) ^ ((d1>>>5) + k1);

      }
      encodeInt[i] = v0;
      encodeInt[i+1] = v1;

      System.out.format("%h", v0);
      System.out.format("%h", v1);
      System.out.format("\n");
      //i+=2;
    }
  }








}
