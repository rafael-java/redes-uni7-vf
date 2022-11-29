package entities;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Host implements No {

	private Map<String, String> tabArp = new HashMap<>();
	private Map<String, Porta> tabEnc = new HashMap<>();
	private PortaHost portaHost;
	private Queue<Pacote> filaEnc =  new LinkedList<Pacote>();
	
	public Host() {
	}

	public void enviar(Ip ipDestino, String payload) {
		System.out.println("Host enviando...");
		Pacote pkg = new Pacote(this.portaHost.getMacAddress(), this.portaHost.getIp(), ipDestino, payload);

		String macDestino = buscarARP(ipDestino.toString());

		if(macDestino != null) {
			System.out.println("Encontrado o Mac Destino na ARP table...");
			pkg.setMacDestino(macDestino);
			
			this.portaHost.enviar(pkg);
		} else {
			
			System.out.println("Não encontrado o Mac Destino na ARP table...");

			System.out.println("Adicionando o pacote na fila de pacotes do Host...");
			filaEnc.add(pkg);
			
			Pacote arpPkg = new Pacote(this.portaHost.getMacAddress(), "FF:FF:FF:FF:FF:FF", this.portaHost.getIp(), ipDestino, true);
			
			this.portaHost.enviar(arpPkg);
		}		
	}
	
	public void receber(Pacote pacote, Porta portaHostQueRecebeu) {
		
		System.out.println("Host recebendo o pacote...");
		
		System.out.println("Colocando na ENC table os dados do \"acusado\", caso já não estejam......");
		this.tabEnc.put(pacote.getMacOrigem(), portaHostQueRecebeu);
		
		System.out.println("Colocando na ARP table os dados do \"acusado\", caso já não estejam......");
		this.tabArp.put(pacote.getIpOrigem().toString(), pacote.getMacOrigem());

		ler(pacote);
	}
	
	public void ler(Pacote pacote) {
		
		System.out.println("Host lendo o pacote...");

		if (pacote.getMacDestino().equals("FF:FF:FF:FF:FF:FF") && pacote.getPayload().equals("Request") && pacote.getIpDestino().equals(this.portaHost.getIp())) {
			System.out.println("Pacote é um ARP request, e o host ("+this.getPortaHost().getMacAddress()+") é o destinatário...");
			Pacote pReply =  new Pacote(this.portaHost.getMacAddress(), pacote.getMacOrigem(), this.portaHost.getIp(), pacote.getIpOrigem(), false);	
			
			this.portaHost.enviar(pReply);
			}
		
		else if(pacote.getPayload().equals("Reply")) {
			System.out.println("Host recebeu um pacote reply, será que isso atualizou algo para os pacotes que estavam na fila? Vamos ver...");
			for (Pacote pacote2 : filaEnc) {
				pacote2 = filaEnc.poll();
				System.out.println("");
				System.out.println("Há pacote na fila, obtendo pacote...");
				System.out.println("Tentando o envio...");
				this.enviar(pacote2.getIpDestino(), pacote2.getPayload());
			}
		} else {
			System.out.println("O host é o destinatario ou o remetente, não é papel da camada de enlace lidar com isso");
			System.out.println("      PARA TESTE:      \n "
							+  "______________Pacote______________");
			System.out.println("| IP Destino: "+pacote.getIpDestino());
			System.out.println("| IP Origem: "+pacote.getIpOrigem());
			System.out.println("| MAC Destino: "+pacote.getMacDestino());
			System.out.println("| MAC Origem: "+pacote.getMacOrigem());
			System.out.println("| Payload: "+pacote.getPayload());
			System.out.println("__________Placa deste host_________");
			System.out.println("| MAC: "+this.portaHost.getMacAddress());
			System.out.println("| IP: "+this.portaHost.getIp());
			System.out.println("-----------------------------------------");
		}
		
		// Ao receber o ArpReply optamos por ser unicast
	}
	
	private String buscarARP(String ip) {
		System.out.println("Buscando na ARP table do host...");
		return this.tabArp.get(ip);
	}
	
	private Porta buscarEnc(String macAddress) {
		System.out.println("Buscando na ENC table do host...");
		return this.tabEnc.get(macAddress);
	}

	
	public Map<String, String> getTabArp() {
		return tabArp;
	}

	public void setTabArp(Map<String, String> tabArp) {
		this.tabArp = tabArp;
	}

	public Map<String, Porta> getTabEnc() {
		return tabEnc;
	}

	public void setTabEnc(Map<String, Porta> tabEnc) {
		this.tabEnc = tabEnc;
	}

	public PortaHost getPortaHost() {
		return portaHost;
	}

	public void setPortaHost(PortaHost portaHost) {
		this.portaHost = portaHost;
	}

	public Queue<Pacote> getFila() {
		return filaEnc;
	}

	public void setFila(Queue<Pacote> fila) {
		this.filaEnc = fila;
	}
	
}
