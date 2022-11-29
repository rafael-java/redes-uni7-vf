package entities;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import helpers.StringExtension;

public class Network {

    private final Ip ip;

    private int mask;

    private final char SEPARATOR = '/';

    public Network(Ip ip, int mask)
    {
        this.ip = ip;

        setMask(mask);
    }

    private void setMask(int mask)
    {
        if(mask <= ip.getMaxBits())
        {
            this.mask = mask;
        }
    }

    public int getMask() {
        return mask;
    }

    public Ip getIp() {
        return ip;
    }

    public Ip getLastIp()
    {
        Ip lastIp = new Ip();

        int compBitsAmount = ip.getMaxBits() - mask;

        for(int blockPosition = ip.getMaxBlocksAmount(); blockPosition > 0; blockPosition--){

            int bitsOperationAmount = compBitsAmount >= ip.getBitsBlockAmount() ? ip.getBitsBlockAmount() : compBitsAmount % ip.getBitsBlockAmount();

            String bitsOperation = bitsOperationAmount > 0 ? StringExtension.repeat("1",bitsOperationAmount) : "0";

            int blockValue = ip.getPosition(blockPosition) | Integer.parseInt(bitsOperation, 2);

            lastIp.setPosition(blockPosition, blockValue);

            compBitsAmount -= bitsOperationAmount;
        }

        return lastIp;
    }

    public Ip getFirstIp()
    {
        return getIp();
    }
    
    public Ip addressNetwork() {
    	Ip ip = new Ip();
    	String ipbinary = this.ip.getIpBinary();
    	String maskBits = String.format("%-" + this.ip.getMaxBits() + "s", StringExtension.repeat("1",getMask())).replace(' ', '0');
    	
    	String ipNetworkBits = Long.toBinaryString(Long.parseLong(ipbinary, 2) & Long.parseLong(maskBits, 2));

    	ip.setPartsByBits(ipNetworkBits);
    	
    	return ip;
    }
    
    public Ip lastPossibleIp() {
    	Ip ip = new Ip();
    	
    	String ipbinary = this.ip.getIpBinary();
    	
    	String maskBits = StringExtension.repeat("1",32 - getMask());
    	
    	String lastIpBit = Long.toBinaryString(Long.parseLong(ipbinary, 2) | Long.parseLong(maskBits, 2));
    	
    	ip.setPartsByBits(lastIpBit);
    	
    	return ip;
    }
    
    

    public List<IpRange<Ip,Ip>> BreakNetWorkIn(int subnetsAmount) {
    	List<IpRange<Ip, Ip>> ipRanges = new ArrayList<>();
    	
    	if(subnetsAmount > 0) {
    		int amountRanges = (int)Math.ceil(Math.log(subnetsAmount) / Math.log(2));
    		
    		int newMask = this.getMask() + amountRanges;
    		
    		double magicNumber = Math.pow(2, 32 - newMask); //5 bits livres
    		
    		String maskBits = String.format("%-" + this.ip.getMaxBits() + "s", StringExtension.repeat("1",getMask())).replace(' ', '0');
    		
    		String ipBinaryinitial = Long.toBinaryString(Long.parseLong(this.ip.getIpBinary(), 2) & Long.parseLong(maskBits, 2)); // & binario transforma tudo que esta em zero na mascara para zero no ip normal, logo derivzndo o ip inicial porem em binario string.
    		
    		long subnetIp = Long.parseLong(ipBinaryinitial, 2);
    		
    		long number_magic = Math.round(magicNumber);

    		for(int i=1;i<=subnetsAmount;i++) {
    			
    			Ip ip = new Ip();
    			
    			Ip lastip = new Ip();
    			    			
    			ip.setPartsByBits(Long.toBinaryString(subnetIp));
    			
    			subnetIp = subnetIp + (number_magic - 1);
    			
    			lastip.setPartsByBits(Long.toBinaryString(subnetIp));
    			
    			IpRange<Ip, Ip> ipRange = new IpRange<>(ip, lastip);
    			
    			subnetIp = subnetIp + 1;
    			
                ipRanges.add(ipRange);
                
                
    			
    		}
    		
    	} else {
    		
    	}
    	
    	return ipRanges;
    }

    
	public List<IpRange<Ip, Ip>> getIpRanges(int subnetsAmount){
        List<IpRange<Ip, Ip>> ipRanges = new ArrayList<>();

        if(subnetsAmount > 0)
        {
            int ipRangesAmount = (int)Math.ceil(Math.log(subnetsAmount) / Math.log(2));

            if((ip.getMaxBits() - getMask()) < (ip.getBitsBlockAmount() + ipRangesAmount))
            {
                throw new InvalidParameterException("The network cannot provide " + subnetsAmount + " ip ranges.");
            }

            String maskBits = String.format("%-" + this.ip.getMaxBits() + "s", StringExtension.repeat("1",getMask())).replace(' ', '0');

            String ipPartsBits = Long.toBinaryString(Long.parseLong(this.ip.getPartsBits(4), 2) & Long.parseLong(maskBits, 2));

            long subnetIp = Long.parseLong(ipPartsBits, 2);

            int newMask = this.getMask() + ipRangesAmount;
            
            for(int i = 0; i < Math.pow(2, ipRangesAmount); i++)
            {
                Ip ip = new Ip();

                int defaultSum = i > 0 ? ip.getMaxBlockValue() + 1 : 0;

                subnetIp = subnetIp + defaultSum;

                ip.setPartsByBits(Long.toBinaryString(subnetIp));

                Network network = new Network(ip, newMask);

                Ip lastIp = network.getLastIp();

                IpRange<Ip, Ip> ipRange = new IpRange<>(ip, lastIp);

                ipRanges.add(ipRange);
            }
        }

        return ipRanges;
    }

    public boolean ipBelongsToNetwork(Ip ip)
    {
        String maskBits = String.format("%-" + this.ip.getMaxBits() + "s", StringExtension.repeat("1",getMask())).replace(' ', '0');

        String networkPartsBits = Long.toBinaryString(Long.parseLong(this.ip.getPartsBits(4), 2) & Long.parseLong(maskBits, 2));
        String ipPartsBits = Long.toBinaryString(Long.parseLong(ip.getPartsBits(4), 2) & Long.parseLong(maskBits, 2));

        return networkPartsBits.equals(ipPartsBits);
    }

    @Override
    public String toString() {
        return  ip + Character.toString(SEPARATOR) + mask;
    }
    
    
}
