package com.example.chatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private KeyPair keyPair;
    private KeyAgreement keyAgree;
    private byte[] sharedKey;
    private PublicKey publicKey;
    private SharedPreferences sharedPreferences;

    public AES(){
        sharedPreferences = MyApplication.getContext().getSharedPreferences("keys", Context.MODE_PRIVATE);
        getKeyPair();
    }

    public String getPublicKey(){
           return Base64.getEncoder()
                   .encodeToString(publicKey.getEncoded());
    }

    private void getKeyPair() {
        DHParameterSpec dhParamSpec;
        String pubKey = sharedPreferences
                .getString("pubkey",null);
        String privKey = sharedPreferences
                .getString("privateKey",null);
        try{
            if(pubKey==null){
                KeyPairGenerator kpg = null;
                    kpg = KeyPairGenerator.getInstance("EC");
                    kpg.initialize(256);
                    keyPair = kpg.generateKeyPair();
                    publicKey = keyPair.getPublic();

                sharedPreferences.edit().putString("pubkey",Base64.getEncoder()
                        .encodeToString(publicKey.getEncoded())).apply();
                Log.i("pubkey",sharedPreferences.getString("pubkey","resdlkfj"));
                sharedPreferences.edit().putString("privateKey",Base64.getEncoder()
                        .encodeToString(keyPair.getPrivate().getEncoded())).apply();

            }
            else{
                byte[] byte_pubkey  = Base64.getDecoder().decode(pubKey);
                byte[] byte_privKey = Base64.getDecoder().decode(privKey);
                KeyFactory factory = KeyFactory.getInstance("EC");
                publicKey = factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
                Log.i("public key",Base64.getEncoder().encodeToString(publicKey.getEncoded()));
                keyPair = new KeyPair(publicKey,factory
                        .generatePrivate(new PKCS8EncodedKeySpec(byte_privKey)));
            }
            keyAgree = KeyAgreement.getInstance("ECDH");
            keyAgree.init(keyPair.getPrivate());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void computeSharedKey(String publicKey) {
        PublicKey pbKey = null;
        byte[] pubKeyBytes = Base64.getDecoder().decode(publicKey);
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("EC");
            pbKey = factory.generatePublic(new X509EncodedKeySpec((pubKeyBytes)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }


        try {
            keyAgree.doPhase(pbKey, true);
            sharedKey = keyAgree.generateSecret();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        Log.i("shared key",Base64.getEncoder().encodeToString(sharedKey));
    }
    public String encrypt(String msg) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sharedKey,"AES"));
            byte[] encVal = c.doFinal(msg.getBytes());
            return Base64.getEncoder().encodeToString(encVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
    public String decrypt(String encryptedData) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sharedKey,"AES"));
            byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
            byte[] decValue = c.doFinal(decordedValue);
            return new String(decValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedData;
    }


}
