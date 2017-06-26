package sample;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Date;

/**
 * Created by GCUK-SD on 16/06/2017.
 */
public class Block {

    public String data;
    public String from;
    public String to;
    public int amount;
    public String previousHash;
    public String currentHash;

    Block(String from, String to, int amount, String previousHash)
    {

        this.from = from;
        this.to = to;
        this.amount = amount;
        data = String.format("From: %s\nTo: %s\nAmount: %s\n", from, to, amount);
        this.previousHash = previousHash;
        String toBeHashed = data+previousHash;
        currentHash =  hash(toBeHashed);

    }

    public String hash(String data)
    {

        try
        {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String text = data;

            md.update(text.getBytes("UTF-16"));
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte Byte : digest)
            {

                sb.append(String.format("%02X", Byte));

            }

            return sb.toString();

        }
        catch(Exception ex) {

            ex.printStackTrace();

        }
        return null;
    }

    public void setAmount(int amount)
    {

        this.amount = amount;

    }

    public void setFrom(String from)
    {

        this.from = from;

    }

    public void setTo(String to)
    {

        this.to = to;

    }

    public void setCurrentHash(String currentHash)
    {

        this.currentHash = currentHash;

    }

    public void setPreviousHash(String previousHash)
    {

        this.previousHash = previousHash;

    }

    public void print()
    {

        System.out.println(String.format("from:%s,to:%s,amount:%s,previousHash:%s,currentHash:%s", from, to, amount, previousHash, currentHash));

    }

}
