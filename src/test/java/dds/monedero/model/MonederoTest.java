package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void ponerUnSoloDeposito() {
    cuenta.poner(1500);
    assertEquals(cuenta.getSaldo(), 1500);
  }

  @Test
  void ponerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void ponerTresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(cuenta.getSaldo(), 3856);
  }

  @Test
  void masDeTresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(245);
    });
  }

  @Test
  void extraer() {
    cuenta.setSaldo(1000);
    cuenta.sacar(500);
    assertEquals(cuenta.getSaldo(), 500);
  }

  @Test
  void extraerMasQueElSaldo() {
    cuenta.setSaldo(90);
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.sacar(1001);
    });
  }

  @Test
  public void extraerMasDe1000() {
    cuenta.setSaldo(5000);
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.sacar(1001);
    });
  }

  @Test
  public void extraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  public void montoExtraidoAyer() {
    cuenta.agregarMovimiento(new Extraccion(LocalDate.now(), 2000));
    cuenta.agregarMovimiento(new Extraccion(LocalDate.now().minusDays(1), 1000));
    cuenta.agregarMovimiento(new Extraccion(LocalDate.now().minusDays(1), 1500));
    cuenta.agregarMovimiento(new Extraccion(LocalDate.now().minusDays(1), 500));
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.now().minusDays(1)), -3000);
  }

  @Test
  public void movimientosRealizadosHoy() {
    cuenta.poner(1500);
    cuenta.sacar(500);
    cuenta.poner(456);
    cuenta.sacar(250);
    cuenta.poner(1900);
    cuenta.sacar(1500);
    assertEquals(cuenta.getMovimientos().size(), 6);
  }
}