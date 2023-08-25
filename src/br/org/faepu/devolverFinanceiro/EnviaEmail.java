package br.org.faepu.devolverFinanceiro;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EnviaEmail {
	
	String msg;
	String corpoEmail;
	
	BigDecimal codFila = null;
	
	public void EnviarEmail(BigDecimal usuarioLogado, Timestamp agora, BigDecimal nuFin, BigDecimal nuNota) {
		
		JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(jdbc);
		
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		jdbc = dwfEntityFacade.getJdbcWrapper();
		JapeSession.SessionHandle hnd = JapeSession.open();
		
		System.out.println("Entrou no metodo enviar email.");
		
		corpoEmail = "  <h2>CONTROLADORIA</h2>\r\n"
				+ "           <p><span style=\"font-size:14px\"><b>O FINANCEIRO DO NÚMERO ÚNICO DE ORIGEM :</b></span>\r\n"
				+ "		   <span style=\"font-size:16px\"><span style=\"color:#FF0000\"><b>"+nuNota+"</span></span></b><span style=\"font-size:14px\">\r\n"
				+ "            <b>FOI DEVOLVIDO PELO FINANCEIRO CONFORME SOLICITADO. </b></span><span style=\"font-size:16px\">\r\n"
				+ "           <p><span style=\"font-size:14px\"><b>APÓS OS AJUSTES ELE DEVERÁ SER REENVIADO AO FINANCEIRO CLICANDO NO MENU DO \"RAIO\" E SELECIONANDO A OPÇÃO \"Sv Adm - Reenviar \".</b></span><br/>\r\n"
				+ "           <p><span style=\"font-size:14px\"><b>NÃO RESPONDA ESTE E-MAIL!<br></b></span><span style=\"color:#696969\"\"><span style=\"font-size:12px\">\r\n"
				+ "\r\n"
				+ "<p><strong>ATENCIOSAMENTE</strong></p>\r\n"
				+ " ";
		char[] corpoEmailchar = corpoEmail.toCharArray();
		
		// devolução de pedidos Taxas Administrativas para Controladoria!
		
		try {
			
			DynamicVO filaMensagemVO = (DynamicVO) dwfEntityFacade
					.getDefaultValueObjectInstance("MSDFilaMensagem");
			filaMensagemVO.setProperty("ASSUNTO", "Devolução de taxas Administrativa");
			filaMensagemVO.setProperty("CODMSG", null);
			filaMensagemVO.setProperty("DTENTRADA", agora);
			filaMensagemVO.setProperty("STATUS", "Pendente");
			filaMensagemVO.setProperty("CODCON", new BigDecimal(0));
			filaMensagemVO.setProperty("TENTENVIO", new BigDecimal(0));
			filaMensagemVO.setProperty("MENSAGEM", corpoEmailchar);
			filaMensagemVO.setProperty("TIPOENVIO", "E");
			filaMensagemVO.setProperty("MAXTENTENVIO", new BigDecimal(3));
			//filaMensagemVO.setProperty("EMAIL", "t.santos.vasconcelos@gmail.com");
		    //filaMensagemVO.setProperty("EMAIL", "tassio@faepu.org.br");
		    filaMensagemVO.setProperty("EMAIL", "controladoria@faepu.org.br");
			//filaMensagemVO.setProperty("CODSMTP", new BigDecimal(2));
			filaMensagemVO.setProperty("CODUSUREMET", usuarioLogado);

			PersistentLocalEntity createFilaMensagem = dwfEntityFacade.createEntity("MSDFilaMensagem",
					(EntityVO) filaMensagemVO);
			filaMensagemVO = (DynamicVO) createFilaMensagem.getValueObject();
			codFila = filaMensagemVO.asBigDecimal("CODFILA");
			
		}  catch (Exception e) {
			e.printStackTrace();
			msg = "Erro na inclusao do item " + e.getMessage();
			System.out.println(msg);
		}
		
		JapeSession.close(hnd);
	}
}
