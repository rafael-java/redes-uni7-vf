package entities;

import java.util.Objects;
import java.util.regex.Pattern;

public class Ip {

    private int part1;

    private int part2;

    private int part3;

    private int part4;
    
    public String getIpBinary() {
		return ipBinary;
	}

	public void setIpBinary(String ipBinary) {
		this.ipBinary = ipBinary;
	}

	private String ipBinary;

    private final char SEPARATOR = '.';

    private final int MAX_BITS = 32;

    private final int BITS_BLOCK_AMOUNT = 8;

    private final int MAX_BLOCK_VALUE = 255;

    public Ip() { }

    public Ip(String ip) {

        String[] ipSplit = ip.split(Pattern.quote(Character.toString(SEPARATOR)));

        if(ipSplit.length == 4)
        {
            try {
                part1 = Integer.parseInt(ipSplit[0]);
                part2 = Integer.parseInt(ipSplit[1]);
                part3 = Integer.parseInt(ipSplit[2]);
                part4 = Integer.parseInt(ipSplit[3]);
                
                ipBinary = Ip.toBinary(part1) + Ip.toBinary(part2) + Ip.toBinary(part3) + Ip.toBinary(part4);
                
            }
            catch(NumberFormatException ex)
            {
                throw ex;
            }
        }
        else
        {
            throw new IllegalArgumentException("Invalid ip address");
        }
    }

    public void setPart1(int part1) {
        this.part1 = part1;
    }

    public void setPart2(int part2) {
        this.part2 = part2;
    }

    public void setPart3(int part3) {
        this.part3 = part3;
    }

    public void setPart4(int part4) {
        this.part4 = part4;
    }

    public int getPart1() {
        return part1;
    }

    public int getPart2() {
        return part2;
    }

    public int getPart3() {
        return part3;
    }

    public int getPart4() {
        return part4;
    }

    public int getMaxBits()
    {
        return MAX_BITS;
    }

    public int getBitsBlockAmount()
    {
        return BITS_BLOCK_AMOUNT;
    }

    public int getMaxBlocksAmount()
    {
        return MAX_BITS / BITS_BLOCK_AMOUNT;
    }

    public int getMaxBlockValue()
    {
        return MAX_BLOCK_VALUE;
    }
    public void setPosition(int position, int value)
    {
        switch (position) {
            case 1:
            	setPart1(value);
            	break;
            case 2:
            	setPart2(value);
            	break;
            case 3:
            	setPart3(value);
            	break;
            case 4:
            	setPart4(value);
            	break;
        }
    }

    public int getPosition(int position)
    {
    	int result = 0;
    	switch (position) {
	        case 1: 
	        	result=getPart1();
	        	break;
	        case 2: 
	        	result=getPart2();
	        	break;
	        case 3: 
	        	result=getPart3();
	        	break;
	        case 4: 
	        	result=getPart4();
	        	break;
	        default: 
	        	result=0;
	        	break;
    };
        return result; 
    }
    
    
    
    public static String toBinary(int x) {
        return String.format("%8s", Integer.toBinaryString(x)).replaceAll(" ", "0");
    }

    public String getPartsBits(int partsAmount)
    {
        StringBuilder partsBits = new StringBuilder();

        for(int i = 1; i <= partsAmount; i++)
        {
            partsBits.append(String.format("%1$" + getBitsBlockAmount() + "s", Integer.toBinaryString(getPosition(i))));
        }

        return partsBits.toString().replace(' ', '0');
    }

    public void setPartsByBits(String bits)
    {
        for(int blockIndex = 1; blockIndex <= getMaxBlocksAmount(); blockIndex++)
        {
            int bitsAmount = Math.min(bits.length(), blockIndex * getBitsBlockAmount());

            if(bits.length() >= blockIndex * getBitsBlockAmount())
            {
                String bitsBlock = bits.substring((blockIndex * getBitsBlockAmount()) - getBitsBlockAmount(), bitsAmount);

                setPosition(blockIndex, Integer.parseInt(bitsBlock, 2));
            }
        }
        ipBinary = Ip.toBinary(part1) + Ip.toBinary(part2) + Ip.toBinary(part3) + Ip.toBinary(part4);
    }

    @Override
    public String toString() {
        return String.valueOf(part1) + SEPARATOR + part2 + SEPARATOR + part3 + SEPARATOR + part4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ip ip = (Ip) o;
        return part1 == ip.part1 && part2 == ip.part2 && part3 == ip.part3 && part4 == ip.part4;
    }

    @Override
    public int hashCode() {
        return Objects.hash(part1, part2, part3, part4, SEPARATOR);
    }
}
