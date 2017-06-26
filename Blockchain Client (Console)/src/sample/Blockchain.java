package sample;

import javafx.concurrent.Task;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by GCUK-SD on 16/06/2017.
 */
public class Blockchain{

    public List<Block> blocks;
    String name;
    Callable<Void> observer;

    Blockchain(Callable<Void> observer)
    {

        Block first = new Block("N/A", "N/A", 0, "000");
        blocks = new ArrayList<>();
        blocks.add(first);
        name = System.getProperty("user.name");
        this.observer = observer;

    }

    public void modify(int i, String from, String to, int amount)
    {

        Block changed = blocks.get(i);
        changed.from = from;
        changed.to = to;
        changed.amount = amount;
        changed.data = String.format("From: %s\nTo: %s\nAmount: %s\n", from, to, amount);
        rehash(i);

    }

    public void rehash(int i)
    {
        if (i > 0)
        {

            for (int j = i; j < blocks.size(); j++)
            {

                String previousHash = blocks.get(j-1).currentHash;
                blocks.get(i).previousHash = previousHash;
                blocks.get(i).currentHash = hash(blocks.get(i).data+previousHash);

            }

        }

    }

    public String getTailHash()
    {

        return blocks.get(blocks.size()-1).currentHash;

    }

    public void append(String from, String to, int amount)
    {

        Block last = new Block(from, to, amount, blocks.get(blocks.size()-1).currentHash);
        blocks.add(last);
        try
        {

            observer.call();

        }
        catch (Exception e)
        {

            e.printStackTrace();

        }

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

    public void print()
    {
        int i = 0;
        for (Block b : blocks)
        {

            System.out.println(String.format("%s--------------\nFrom: %s\nTo: %s\nAmount: %s\nPrevious hash: %s\nCurrent hash: %s\n--------------\n", i, b.from, b.to, b.amount, b.previousHash, b.currentHash));
            i++;

        }

    }


}
