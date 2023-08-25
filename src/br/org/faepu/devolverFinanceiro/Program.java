package br.org.faepu.devolverFinanceiro;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.ws.ServiceContext;

public class Program implements AcaoRotinaJava {

	Timestamp agora = new Timestamp(System.currentTimeMillis());

	BigDecimal nuNota;
	BigDecimal usoLogado;
	int codTipOper;
	BigDecimal nuFin;

	String provisao;

	int devFin;

	Timestamp dhBaixa;

	@Override
	public void doAction(ContextoAcao ctx) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Inicio do codigo ");

		JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(JDBC);
		JapeSession.SessionHandle hnd = JapeSession.open();

		AlteraCampoDEVFIN alteraCampo = new AlteraCampoDEVFIN();
		EnviaEmail enviarEamil = new EnviaEmail();
		Historicos his = new Historicos();

		BigDecimal usuarioLogado = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUserID();

		System.out.println("Usuario logado :" + usuarioLogado);

		for (int i = 0; i < (ctx.getLinhas()).length; i++) {
			Registro linha = ctx.getLinhas()[i];

			nuFin = (BigDecimal) linha.getCampo("NUFIN");
			System.out.println(nuFin);
		}

		ResultSet rs = nativeSql.executeQuery(
				"SELECT CODTIPOPER, PROVISAO, DHBAIXA, AD_DEVFIN, NUNOTA FROM TGFFIN " + " WHERE NUFIN = " + nuFin);

		System.out.println("resultSet" + rs);

		while (rs.next()) {
			codTipOper = rs.getInt("CODTIPOPER");
			provisao = rs.getString("PROVISAO");
			dhBaixa = rs.getTimestamp("DHBAIXA");
			devFin = rs.getInt("AD_DEVFIN");
			nuNota = rs.getBigDecimal("NUNOTA");

			System.out.println("");
			System.out.println(codTipOper);
			System.out.println(provisao);
			System.out.println(dhBaixa);
			System.out.println(devFin);
			System.out.println(nuNota);
		}

		if (codTipOper == 184 || codTipOper == 210) {

			if (provisao.equals("S")) {
				ctx.setMensagemRetorno("Título ainda não foi liberado!");
				return;
			}

			if (dhBaixa != null) {
				ctx.setMensagemRetorno("Título já foi baixado e não poderá ser devolvido!");
				return;
			}

			if (devFin == 1) {
				ctx.mostraErro("Titulo Ja foi Devolvido");
				return;
			}

			alteraCampo.DEVFIN(nuFin, usuarioLogado);

			enviarEamil.EnviarEmail(usuarioLogado, agora, nuFin, nuNota);

			his.InserirHistorico(usuarioLogado, nuFin, nuNota);

			ctx.setMensagemRetorno("Título devolvido com sucesso!");

		} else {
			ctx.setMensagemRetorno("A TOP informada não pode ser devolvida!");
			return;

		}

		JapeSession.close(hnd);

	}

}
