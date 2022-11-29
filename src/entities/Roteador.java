package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import helpers.NetworkExtension;

public class Roteador implements No {

	private Map<String, String> tabArp = new HashMap<>();
	private Map<String, Porta> tabEnc = new HashMap<>();
	private Map<Network, PortaRoteador> tabRots = new HashMap<>();
	private Queue<Pacote> filaEnc = new LinkedList<Pacote>();
	private List<PortaRoteador> interfaces;
	private Network ownNetwork;
	
	public Roteador(Map<Network, PortaRoteador> tabRots, Network ByNetwork) {
		this.ownNetwork= ByNetwork;
		this.tabRots = tabRots; // eh quando conecta os cabos
	}

	private PortaRoteador getMostSpecificNetworkFromTabRots(String ip) {
		System.out.println("Obtendo a rede mais específica que tem o IP informado como filho");
		List<Network> networks = new ArrayList<Network>();
		for (Network ipRot : tabRots.keySet()) {
			networks.add(ipRot);
		}
		Network ownerNetwork = NetworkExtension.ipNetworkOwner(networks, new Ip(ip));
		return this.tabRots.get(ownerNetwork);
	}
	
	@Override
	public void receber(Pacote pacoteAtual, Porta portaRoteadorQueRecebeu) {
		
		System.out.println("Roteador recebendo o pacote...");
		
		System.out.println("Colocando na ENC table os dados do \"acusado\", caso já não estejam......");
		this.tabEnc.put(pacoteAtual.getMacOrigem(), portaRoteadorQueRecebeu);
		
		System.out.println("Colocando na ARP table os dados do \"acusado\", caso já não estejam......");
		this.tabArp.put(pacoteAtual.getIpOrigem().toString(), pacoteAtual.getMacOrigem());
		
		// Obtendo a interface do roteador que recebeu o pacote
		Integer index = this.getInterfaces().indexOf(portaRoteadorQueRecebeu);
		PortaRoteador interfaceRoteadorQueRecebeu = this.getInterfaces().get(index);
		
		System.out.println("\nVerificando a situação do roteador...");
		boolean isFromMyDomain = ownNetwork.ipBelongsToNetwork(pacoteAtual.getIpOrigem());
		boolean isToMyDomain = ownNetwork.ipBelongsToNetwork(pacoteAtual.getIpDestino());
		
		if(pacoteAtual.getPayload().equals("Reply") && isFromMyDomain) { // ARP Reply recebido...
			System.out.println("ARP Reply recebido de alguém da rede do roteador...");

			Pacote pacoteToForward = null;
			
			System.out.println("Caso haja, para cada pacote na fila, verifica se o IP origem é igual ao IP de destino do ARP Reply...");
			for (Pacote pacoteOriginal : filaEnc) {
				if(pacoteOriginal.getIpOrigem().equals(pacoteAtual.getIpDestino())) {
					System.out.println("Há. Seta o pacote para transmitir...");
					pacoteToForward = pacoteOriginal;
					break;
				}
			}
			
			if(pacoteToForward != null) {
				System.out.println("Seta o Mac Destino do pacote para transmitir como sendo o Mac Origem do ARP Reply ...");
				pacoteToForward.setMacDestino(pacoteAtual.getMacOrigem());
				
				System.out.println("Devolve o pacote pela porta...");
				portaRoteadorQueRecebeu.enviar(pacoteToForward);
			}
			
			return;
		}
		
		if(isFromMyDomain && isToMyDomain) { // Eu ajo como um switch. Não estamos usando esse cenário na simulação. 
			System.out.println("O pacote é do domínio atual do roteador e vai para alguém no domínio atual do roteador...");
			
			if (pacoteAtual.getMacDestino().equals("FF:FF:FF:FF:FF:FF")) {
				System.out.println("O pacote tem Mac Destino igual a FF.FF.FF.FF.FF.FF...");
				this.broadcast(pacoteAtual);
			} else {
				System.out.println("O pacote tem Mac Destino diferente de FF.FF.FF.FF.FF.FF...");
				Porta portaAEnviar = this.buscarEnc(pacoteAtual.getMacDestino());

				if (portaAEnviar == null) {
					System.out.println("Não localizou a porta na tabela ENC...");	
					this.broadcast(pacoteAtual);
				}

				else {
					System.out.println("Localizou a porta na tabela ENC...");
					this.encaminhar(pacoteAtual, portaAEnviar);
				}
			}
		} else if (!isFromMyDomain && isToMyDomain){ // Eu sou roteador receptor
		
			System.out.println("O pacote não é do domínio atual do roteador, mas vai para alguém no domínio atual do roteador...");
			
			System.out.println("Busca na ARP table");
			String macDestino = buscarARP(pacoteAtual.getIpDestino().toString());
		
			if(macDestino != null) {
				System.out.println("Encontrado o Mac Destino na ARP table...");
				
				System.out.println("Seta o Mac Destino do pacote...");
				pacoteAtual.setMacDestino(macDestino);
				
				System.out.println("Seta o Mac Origem do pacote como sendo o Mac da interface...");
				pacoteAtual.setMacOrigem(interfaceRoteadorQueRecebeu.getMacAddress()); 
				
				System.out.println("Obtem a porta (interface) para enviar o pacote...");
				Porta port = this.tabEnc.get(macDestino);
				port.enviar(pacoteAtual);
				
			} else {
				System.out.println("Não encontrado o Mac Destino na ARP table...");
				
				System.out.println("Seta o Mac Origem do pacote como sendo o Mac da interface...");
				pacoteAtual.setMacOrigem(interfaceRoteadorQueRecebeu.getMacAddress()); 
 
				System.out.println("- - - Adiciona o pacote na fila encaminhamento do Roteador...");
				filaEnc.add(pacoteAtual);
				
				for (PortaRoteador inter : this.getInterfaces()) {
					if(inter.equals(interfaceRoteadorQueRecebeu)) continue; // faz todas, exceto essa (not if)
					
					System.out.println("Cria um pacote ARP request");
					Pacote arpPkg = new Pacote(inter.getMacAddress(), "FF:FF:FF:FF:FF:FF", pacoteAtual.getIpOrigem(), pacoteAtual.getIpDestino(), true);
					
					System.out.println("Encaminha o arp request pelo cabo");
					if (inter.getCabo() != null) {
						this.encaminhar(arpPkg, inter);
					}
				}
			}
		} else if(!isToMyDomain) { // Eu sou um roteador normal
			
			System.out.println("O pacote não vai para alguém no domínio atual do roteador...");

			if (pacoteAtual.getMacDestino().equals("FF:FF:FF:FF:FF:FF") && isFromMyDomain) { // Arp Request recebido...
				System.out.println("Pacote ARP Request recebido, e é do domínio atual do roteador...");
				
				System.out.println("Verifica se há rede na tabela de rotas para ser encaminhado...");
				PortaRoteador interfac = getMostSpecificNetworkFromTabRots(pacoteAtual.getIpDestino().toString());
				
				if(interfac != null) {
					System.out.println("Achou uma rota... Cria um arp reply...");
					Pacote pReply =  new Pacote(interfaceRoteadorQueRecebeu.getMacAddress(), pacoteAtual.getMacOrigem(), pacoteAtual.getIpDestino(), pacoteAtual.getIpOrigem(), false);
					
					System.out.println("Devolve para quem enviou...");
					interfaceRoteadorQueRecebeu.enviar(pReply);
					
					return;
				} else {
					System.out.println("404 Not Found na table de rotas. Não faz nada");
				}
				
			} else {
				System.out.println("Pacote Normal recebido..."); // Pacote normal recebido, roteador normal...
				
				System.out.println("Verifica se há rede na tabela de rotas para ser encaminhado...");
				PortaRoteador interfac = getMostSpecificNetworkFromTabRots(pacoteAtual.getIpDestino().toString());
				
				if(interfac != null) {
					System.out.println("Achou uma rota... Seta o Mac Origem do pacote como sendo o Mac da interface...");
					pacoteAtual.setMacOrigem(interfaceRoteadorQueRecebeu.getMacAddress());
					
					System.out.println("Encaminha...");
					interfac.enviar(pacoteAtual);
				} else {
					System.out.println("404 Not Found na table de rotas. Não faz nada");
				}
			}
		}
	}
	
	private String buscarARP(String ip) {
		System.out.println("Buscando na ARP table do roteador...");
		return this.tabArp.get(ip);
	}
	
	
	private void encaminhar(Pacote pacote, Porta porta) {
		System.out.println("");
		System.out.println("Roteador encaminhando o pacote para a (próxima) interface...");

		porta.enviar(pacote);
	}
	
	private void broadcast(Pacote pacote) {
		System.out.println("Iniciando broadcast para todas as portas...");

		for (PortaRoteador inter : this.getInterfacesFromOwnNetwork()) {
			if (inter.getCabo() != null) {
				this.encaminhar(pacote, inter);
			}
		}
	}
	
	private List<PortaRoteador> getInterfacesFromOwnNetwork(){
		System.out.println("Obtendo as interfaces que equivalem à mesma rede do roteador");

		List<PortaRoteador> ports = new ArrayList<PortaRoteador>();
		
		for(PortaRoteador port : this.getInterfaces()) {
			if(this.ownNetwork.ipBelongsToNetwork(port.getIp())) {
				ports.add(port);
			}
		}
		
		return ports;
	}
	
	private Porta buscarEnc(String macAddress) {
		System.out.println("Buscando na tabela ENC do roteador...");

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
	

	public Map<Network, PortaRoteador> getTabRots() {
		return tabRots;
	}
	

	public void setTabRots(Map<Network, PortaRoteador> tabRots) {
		this.tabRots = tabRots;
	}
	

	public Queue<Pacote> getFilaEnc() {
		return filaEnc;
	}
	

	public void setFilaEnc(Queue<Pacote> filaEnc) {
		this.filaEnc = filaEnc;
	}
	

	public List<PortaRoteador> getInterfaces() {
		return interfaces;
	}
	

	public void setInterfaces(List<PortaRoteador> interfaces) {
		this.interfaces = interfaces;
	}
}
