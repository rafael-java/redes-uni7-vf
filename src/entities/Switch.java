package entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Switch implements No {

	private Queue<Pacote> filaEnc = new LinkedList<Pacote>();
	// IP, MAC
	private Map<String, Porta> tabEnc = new HashMap<>();
	private List<PortaSwitch> ports;
	// Lista de Porta

	public Switch() {
	}

	public PortaSwitch getPrimeiraPortaDesconectada() {
		System.out.println("Obtendo primeira porta desconectada, do switch...");

		for (PortaSwitch porta : this.ports) {
			if (!porta.getLigado()) {
				return porta;
			}
		}
		
		return null;
	}

	public void receber(Pacote pacoteAtual, Porta portaQueRecebeu) {
		System.out.println("Switch recebendo...");

		// ENC - MAC, Porta
		// ARP - IP, MAC
		// 1. Adiciona na Tabela Enc e Tabela Arp, respectivamente o Mac Address/Porta e
		// Ip/Mac Address
		System.out.println("Colocando na ENC table os dados do \"acusado\", caso já não estejam...");
		
		this.tabEnc.put(pacoteAtual.getMacOrigem(), portaQueRecebeu);
		
		// 2. caso seja FFF no mac Destino, verifica-se na Tabela Arp caso nao
		// verifica-se na Tabela Enc
		// 3. Apos verificar-se na tabela, caso esteja na tabela executa o
		// encaminhamento caso nao executa BroadCast
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
	}

	private void encaminhar(Pacote pacote, Porta porta) {
		System.out.println("");
		System.out.println("Switch encaminhando o pacote para a (próxima) porta switch...");

		porta.enviar(pacote);
	}

	private void broadcast(Pacote pacote) {
		System.out.println("Iniciando broadcast para todas as portas...");

		// Enviar para todas as Portas do Switch o Pacote, Executando um Flooding na
		// rede, Lembrando que
		// nao espera-se resposta de ninguem.

		for (PortaSwitch portaSwitch : this.getPorts()) {
			if (portaSwitch.getCabo() != null) {
				this.encaminhar(pacote, portaSwitch);
			}
		}
	}

	private Porta buscarEnc(String macAddress) {
		System.out.println("Buscando na tabela ENC do switch...");

		return this.tabEnc.get(macAddress);
	}

	public Queue<Pacote> getFila() {
		return filaEnc;
	}

	public void setFila(Queue<Pacote> fila) {
		this.filaEnc = fila;
	}

	public Map<String, Porta> getTabEnc() {
		return tabEnc;
	}

	public void setTabEnc(Map<String, Porta> tabEnc) {
		this.tabEnc = tabEnc;
	}

	public List<PortaSwitch> getPorts() {
		return ports;
	}

	public void setPorts(List<PortaSwitch> ports) {
		this.ports = ports;
		System.out.println("Setando portas no switch...");
	}


}
