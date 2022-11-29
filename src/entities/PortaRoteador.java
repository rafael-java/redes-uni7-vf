package entities;

import java.security.InvalidParameterException;

import helpers.Singleton;

public class PortaRoteador extends Porta {

	private Roteador roteador;

	private Ip ip;
	
	public PortaRoteador(String macAddress, Ip ip, Roteador roteador) throws InvalidParameterException {
		super(macAddress);
		Singleton.getInstance();
		if (!Singleton.checkIfExists(ip.toString())) {
			this.ip = ip;
			this.roteador = roteador;
		} else {
			throw new InvalidParameterException("Mac ou ip já existe na rede");
		}
	}
	   
	public PortaRoteador(String macAddress, Ip ip) throws InvalidParameterException {
		super(macAddress);
		Singleton.getInstance();
		if (!Singleton.checkIfExists(ip.toString())) {
			this.ip = ip;
		} else {
			throw new InvalidParameterException("Mac ou ip já existe na rede");
		}
	}
	
	@Override
	public void receber(Pacote pacote) {
		System.out.println("Recebendo na porta roteador...");
		roteador.receber(pacote, this);
	}
	
	public Ip getIp() {
		return ip;
	}

	public void setIp(Ip ip) {
		this.ip = ip;
	}
	public Roteador getRoteador() {
		return roteador;
	}

	public void setRoteador(Roteador roteador) {
		this.roteador = roteador;
	}

}
