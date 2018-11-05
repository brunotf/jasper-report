package controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistence.GenericDao;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;

@WebServlet("/relatorio")
public class RelatorioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RelatorioServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		geraRelatorio(request, response);
	}

	private void geraRelatorio(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String erro = "";
		String empresa = request.getParameter("empresa");
		String jasper = "WEB-INF/report/relatorio.jasper";
		ServletContext contexto = getServletContext();

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("empresa", empresa);

		// param.put("figura", contexto.getRealPath("figura"));
		
		byte[] bytes = null;
		
		try {
			JasperReport relatorio = (JasperReport) JRLoader.loadObjectFromFile(contexto.getRealPath(jasper));
			bytes = JasperRunManager.runReportToPdf(relatorio, param, new GenericDao().getConnection());
		} catch (JRException e) {
			erro = e.getMessage();
		} finally {
			if (bytes != null) {
				response.setContentType("application/pdf");
				response.setContentLength(bytes.length);
				ServletOutputStream saida = response.getOutputStream();
				saida.write(bytes);
				saida.flush();
				saida.close();
			} else {
				RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
				dispatcher.forward(request, response);
			}
		}

	}

}
