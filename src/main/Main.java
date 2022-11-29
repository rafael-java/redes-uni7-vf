package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import entities.Cabo;
import entities.Host;
import entities.Ip;
import entities.Network;
import entities.PortaHost;
import entities.PortaRoteador;
import entities.PortaSwitch;
import entities.Roteador;
import entities.Switch;

public class Main {
	
	public static void enviar() {
		
		try {
			System.out.println("Iniciando...");
			
			System.out.println("Criando cabo 1...");
			Cabo cabo1 = new Cabo();
		    
			System.out.println("Criando host origem...");
			Host hostOrigem = new Host();
			
			System.out.println("Criando porta host origem...");
			PortaHost portaOrigem = new PortaHost("00:00:5e:00:53:af", new Ip("168.192.0.132"),hostOrigem);
			
			System.out.println("Setando porta host no host origem...");
			hostOrigem.setPortaHost(portaOrigem);

			System.out.println("Criando switch...");
			Switch swi = new Switch();
			
			System.out.println("Criando lista de portas do switch...");
		    List<PortaSwitch> switchPorts = new ArrayList<PortaSwitch>();
			
			for(Integer i=0;i<=2;i++) {
				Integer j = i + 5;
				System.out.println("Criando e adicionando porta switch na lista de portas do switch...");

				PortaSwitch portaSwitch1 = new PortaSwitch("a"+j.toString()+":f7:df:6c:94:3"+i.toString(),swi);
				switchPorts.add(portaSwitch1);
			}
		    
			System.out.println("Setando lista de portas no switch...");
		    swi.setPorts(switchPorts);
		    
		    System.out.println("Criando cabo 2...");
		    Cabo cabo2 = new Cabo();

		    System.out.println("Criando host destino...");
			Host hostDestino = new Host();
			
			System.out.println("Criando porta host destino...");
			PortaHost portaDestino = new PortaHost("fc:5e:e0:68:ce:94", new Ip("168.192.1.9"), hostDestino);
			
			System.out.println("Setando porta host no host destino...");
			hostDestino.setPortaHost(portaDestino);
			
		    try {
		    	PortaSwitch portaDesconectada1 = swi.getPrimeiraPortaDesconectada();
		    	if (portaDesconectada1 != null) {
		    		cabo1.atrelar(portaOrigem, portaDesconectada1);
		    	} else {
		    		throw new NullPointerException("Não há portas desconectadas");
		    	}

			} catch (Exception e) {
				System.err.println("error: "+ e.getMessage());
				System.exit(0);
			}
		    
			System.out.println("Criando endereço de rede 168.192.0.1/24");
		    Network ownNetwork = new Network(new Ip("168.192.0.1"), 24);
		    
		    System.out.println("Criando endereço de rede 168.192.1.1/24");
		    Network ownNetwork2 = new Network(new Ip("168.192.1.1"), 24);
		    
		    System.out.println("Criando tabela de rotas A");
		    Map<Network, PortaRoteador> tabRotasA = new HashMap<Network, PortaRoteador>();
		    
			System.out.println("Criando e adicionando interfaces roteador na lista de interfaces do roteador A...");
		    PortaRoteador intRotA1 = new PortaRoteador("31:C8:05:05:B3:48", new Ip("168.192.0.2"));
		    PortaRoteador intRotA2 = new PortaRoteador("66:a7:bd:10:f3:05", new Ip("168.192.0.3"));
		    PortaRoteador intRotA3 = new PortaRoteador("c8:9f:18:1a:31:e0", new Ip("168.192.0.4"));
		    PortaRoteador intRotA4 = new PortaRoteador("45:c9:63:be:d7:04", new Ip("168.192.0.5"));
		    List<PortaRoteador> interfacesRot1 = new ArrayList<PortaRoteador>();
		    interfacesRot1.add(intRotA1);
		    interfacesRot1.add(intRotA2);
		    interfacesRot1.add(intRotA3);
		    interfacesRot1.add(intRotA4);
		    
			System.out.println("Criando roteador A...");
		    Roteador rotA = new Roteador(tabRotasA, ownNetwork);

			System.out.println("Setando lista de interfaces no roteador A...");
		    rotA.setInterfaces(interfacesRot1);
		    
			System.out.println("Criando cabo 3...");
		    Cabo cabo3 = new Cabo();
		   		    
			System.out.println("Setando roteador nas interfaces...");
		    intRotA1.setRoteador(rotA);
		    intRotA2.setRoteador(rotA);
		    intRotA3.setRoteador(rotA);
		    intRotA4.setRoteador(rotA);
		    
			System.out.println("Setando a tabela de rotas A");
		    tabRotasA.put(ownNetwork, intRotA1);
		    tabRotasA.put(ownNetwork2, intRotA4);
		    
		    System.out.println("Criando tabela de rotas B");
		    Map<Network, PortaRoteador> tabRotasB = new HashMap<Network, PortaRoteador>();
		    
			System.out.println("Criando e adicionando interfaces roteador na lista de interfaces do roteador B...");
		    PortaRoteador intRotB1 = new PortaRoteador("aa:45:91:5a:40:11", new Ip("168.192.1.2"));
		    PortaRoteador intRotB2 = new PortaRoteador("c1:56:c7:a6:56:cb", new Ip("168.192.1.1"));
		    
			System.out.println("Setando a tabela de rotas B");
		    tabRotasB.put(ownNetwork2, intRotB2);
		    tabRotasB.put(ownNetwork, intRotB1);
		    
			System.out.println("Criando roteador B...");
		    Roteador rotB = new Roteador(tabRotasB, ownNetwork2);
		 	    
			System.out.println("Criando e adicionando interfaces roteador na lista de interfaces do roteador B...");
		    intRotB1.setRoteador(rotB);
		    intRotB2.setRoteador(rotB);
		    
		    List<PortaRoteador> interfacesRot2 = new ArrayList<PortaRoteador>();
		    interfacesRot2.add(intRotB1);
		    interfacesRot2.add(intRotB2);
		    rotB.setInterfaces(interfacesRot2);
		    
		    cabo3.atrelar(intRotA4, intRotB1);
		    cabo2.atrelar(intRotB2, portaDestino);
			System.out.println("... Diretamente no roteador");

			System.out.println("Criando cabo 4...");
		    Cabo cabo4 = new Cabo();
		    
		    PortaSwitch portaDesconectada3 = swi.getPrimeiraPortaDesconectada();
		    if (portaDesconectada3 != null) {
		    	cabo4.atrelar(intRotA1, portaDesconectada3);
	    	} else {
	    		throw new NullPointerException("Não há portas desconectadas");
	    	}
		    
	    	Scanner sc = new Scanner(System.in);

		    while (true) {
		    	System.out.println();
		    	System.out.println("Necessitamos de inputs...");
			    System.out.print("Digite um Payload, ou xxx para sair: ");
			    String payload = sc.nextLine();
			    
			    if (payload.equals("xxx")) {
			    	System.out.println("Saindo...");
			    	sc.close();
			    	break;
			    }
			    
			    System.out.print("Digite um IP de destino (pode digitar 1 para default): ");
			    String ipDestino = sc.nextLine();
			    
			    if (ipDestino.equals("1")) {
			    	ipDestino = "168.192.1.9";
			    }
			    
			    System.out.println("");
			    System.out.println("");
			    
			    hostOrigem.enviar(new Ip(ipDestino), payload);
			    
			    System.out.println("\nContinuando...");
		    }
		    
		} catch (Exception e) {
			System.out.println("Error   " + e.getMessage());
		}   
	}
	

	public static void main(String[] args) {
		enviar();
	}

}
