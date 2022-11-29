package entities;

import java.security.InvalidParameterException;

import helpers.Singleton;

public class PortaHost extends Porta {

	private Host host;
	private Ip ip;
	
	public PortaHost(String macAddress, Ip ip, Host host) throws InvalidParameterException {
		super(macAddress);
		Singleton.getInstance();
		if (!Singleton.checkIfExists(ip.toString())) {
			this.ip = ip;
			this.host = host;
		} else {
			throw new InvalidParameterException("Mac ou ip já existe na rede");
		}
	}
	
	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public void enviar(Pacote pacote) {
		System.out.println("Porta host enviando...");
		super.enviar(pacote);
	}
	
	@Override
	public void receber(Pacote pacote) {
		System.out.println("Recebendo na porta host...");

		this.host.receber(pacote, this);
	}

	public Ip getIp() {
		return ip;
	}

	public void setIp(Ip ip) {
		this.ip = ip;
	}
}
