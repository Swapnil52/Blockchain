package BlockchainServer;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by GCUK-SD on 16/06/2017.
 */
public class Blockchain{

    public List<Block> blocks;
    String name;
    Callable<Void> observer;
    BlockchainDelegate delegate;

    Blockchain()
    {

        Block first = new Block("N/A", "N/A", 0, "000");
        blocks = new ArrayList<>();
        blocks.add(first);
        name = System.getProperty("user.name");
        delegate = new BlockchainDelegate() {
            @Override
            public void didChange() {



            }
        };


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

                Block previous = get(j-1);
                Block current = get(j);
                String data = String.format("From: %s\nTo: %s\nAmount: %s\n", current.from, current.to, current.amount);
                current.setCurrentHash(hash(data+previous.currentHash));

            }

        }

    }

    public Block get(int index)
    {

        return blocks.get(index);

    }

    public String getTailHash()
    {

        return blocks.get(blocks.size()-1).currentHash;

    }

    public void append(String from, String to, int amount)
    {

        Block last = new Block(from, to, amount, blocks.get(blocks.size()-1).currentHash);
        blocks.add(last);
        didChange();

    }

    public Block getLast()
    {

        return blocks.get(blocks.size()-1);

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

    void didChange()
    {

        delegate.didChange();

    }

}


