package entities;

public class Pacote {
	private String macOrigem;
	private Ip ipOrigem;
	private String macDestino;
	private Ip ipDestino;
		
	public Ip getIpOrigem() {
		return ipOrigem;
	}

	public void setIpOrigem(Ip ipOrigem) {
		this.ipOrigem = ipOrigem;
	}

	public Ip getIpDestino() {
		return ipDestino;
	}

	public void setIpDestino(Ip ipDestino) {
		this.ipDestino = ipDestino;
	}

	private String payload;

	public Pacote(String macOrigem, Ip ipOrigem, Ip ipDestino, String payload) {
		System.out.println("");
		System.out.println("Criando um pacote sem MAC destino...");

		this.macOrigem = macOrigem;
		this.ipOrigem = ipOrigem;
		this.ipDestino = ipDestino;
		this.payload = payload;
	}
	
	public Pacote(String macOrigem, String macDestino, Ip ipOrigem, Ip ipDestino, Boolean ARPRequest) {
		this.macOrigem = macOrigem;
		this.ipOrigem = ipOrigem;
		this.macDestino = macDestino;
		this.ipDestino = ipDestino;
		if (ARPRequest) {
			this.payload = "Request";
		} else {
			this.payload = "Reply";
		}
		
		System.out.println("");
		System.out.println("Criando um pacote com MAC destino "+ macDestino + ", e com payload \"" + this.payload + "\"...");
	}
	

	public String getMacOrigem() {
		return macOrigem;
	}

	public void setMacOrigem(String macOrigem) {
		this.macOrigem = macOrigem;
	}



	public String getMacDestino() {
		return macDestino;
	}

	public void setMacDestino(String macDestino) {
		System.out.println("Setando mac destino no pacote...");

		this.macDestino = macDestino;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
