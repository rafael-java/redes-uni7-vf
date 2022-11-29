package entities;

import java.security.InvalidParameterException;

import helpers.Singleton;

public abstract class Porta {
	private Boolean ligado;
	private String macAddress;

	private Cabo cabo;
		
	public Porta(String macAddress) throws InvalidParameterException {
		Singleton.getInstance();
		if (!Singleton.checkIfExists(macAddress)) {
			this.macAddress = macAddress;
			this.ligado = false;
			
		} else {
			throw new InvalidParameterException("Mac ou ip já existe na rede");
		}
	}
	
	public void enviar(Pacote pacote) {
		this.cabo.transmitir(pacote, this);
	}
	
	public abstract void receber(Pacote pacote);

	public Boolean getLigado() {
		return ligado;
	}

	public void setLigado(Boolean ligado) {
		this.ligado = ligado;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public Cabo getCabo() {
		return cabo;
	}

	public void setCabo(Cabo cabo) {
		this.cabo = cabo;
	}
}
