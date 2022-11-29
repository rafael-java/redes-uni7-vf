package entities;

import types.Pair;

public class IpRange<T, U> implements Pair<T, U> {

    private final Ip ip1;
    private final Ip ip2;

    public IpRange(Ip ip1, Ip ip2)
    {
        this.ip1 = ip1;
        this.ip2 = ip2;
    }

    @SuppressWarnings("unchecked")
	@Override
    public T getFirst() {
        return (T) ip1;
    }

    @SuppressWarnings("unchecked")
	@Override
    public U getSecond() {
        return (U) ip2;
    }

    @Override
    public String toString() {
        return "IpRange{" +
                "ip1=" + ip1 +
                ", ip2=" + ip2 +
                '}';
    }
}
