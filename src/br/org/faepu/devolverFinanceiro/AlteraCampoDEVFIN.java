package br.org.faepu.devolverFinanceiro;

import java.math.BigDecimal;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class AlteraCampoDEVFIN {
	
	public void DEVFIN(BigDecimal nuFin, BigDecimal usuarioLogado) throws Exception {
		
		System.out.println("Entou no metodo AlteraCampo");
		
		 JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		 NativeSql nativeSql = new NativeSql(JDBC);
		 JapeSession.SessionHandle hnd = JapeSession.open();
		 
		 System.out.println("nufin dentro do altera campo Devfin" + nuFin);
		
		 boolean update = nativeSql.executeUpdate("UPDATE TGFFIN SET AD_DEVFIN = 1, PROVISAO = 'S' WHERE NUFIN = " + nuFin);
		 
		// JapeWrapper finDAO = JapeFactory.dao(DynamicEntityNames.FINANCEIRO);
		// finDAO.prepareToUpdateByPK(nuFin).set("PROVISAO", "S").set("AD_DEVFIN", new BigDecimal("1")).update();
		 
		 System.out.println(update);
		 System.out.println("aPOS O UPDATE");
		 
		 
		 JapeSession.close(hnd);
	}
}
