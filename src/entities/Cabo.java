package entities;

public class Cabo {
	private Porta ponta1;
	private Porta ponta2;

	public Cabo() {
		super();
	}

	public void transmitir(Pacote pacote, Porta origem) {
		System.out.println("Transmitindo pelo cabo...");
		
		if (ponta1 != null && ponta2 != null && ponta1.getLigado() && ponta2.getLigado()) {
			if (ponta1.equals(origem)) {
				System.out.println("... para a ponta 2, que é do tipo " + this.getPonta2().getClass().getName());
				this.getPonta2().receber(pacote);
			} else {
				System.out.println("... para a ponta 1, que é do tipo " + this.getPonta1().getClass().getName());
				this.getPonta1().receber(pacote);
			}
		} else { throw new NullPointerException("Não há portas deconectadas"); }
	}

	public void atrelar(Porta ponta1, Porta ponta2) {
		System.out.println("Atrelando pontas do cabo");

		this.ponta1 = ponta1;
		System.out.println("Ponta 1 = " + ponta1.getClass().getName());
		this.ponta2 = ponta2;
		System.out.println("Ponta 2 = " + ponta2.getClass().getName());
		
		ponta1.setLigado(true);
		ponta1.setCabo(this);
		
		ponta2.setLigado(true);
		ponta2.setCabo(this);
		
	}

	public Porta getPonta1() {
		return ponta1;
	}

	public void setPonta1(Porta ponta1) {
		this.ponta1 = ponta1;
	}

	public Porta getPonta2() {
		return ponta2;
	}

	public void setPonta2(Porta ponta2) {
		this.ponta2 = ponta2;
	}

}
