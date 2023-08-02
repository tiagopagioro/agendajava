package controller;

import java.io.IOException;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DAO; //class teste com o banco
import model.JavaBeans;

@WebServlet(urlPatterns = {"/main","/insert","/select","/update","/delete","/report"})

/**
 * Servlet implementation class controller
 */
public class controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	DAO dao = new DAO(); //testar o banco 
	JavaBeans contato = new JavaBeans();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public controller() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		//dao.testeConexao();
		
		String action = request.getServletPath();
		System.out.println(action);
		
		if(action.equals("/main")) {
			contatos(request, response);
		} else if(action.equals("/insert")){
			novoContato(request,response);
		} else if(action.equals("/select")){
			listarContatos(request,response);
		} else if(action.equals("/update")){
			editarContatos(request,response);
		} else if(action.equals("/delete")){
			removerContato(request,response);
		} else if(action.equals("/report")){
			gerarRelatorio(request,response);
		}else {
			response.sendRedirect("index.html");
		}
	}
	protected void contatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//response.sendRedirect("agenda.jsp");
		
		//Criando um objeto que irá receber os dados JavaBeans
		ArrayList<JavaBeans> lista = dao.listarContatos();
		
		
		//teste de recebimento da lista
		//for (int i = 0; i < lista.size(); i++) {
			//System.out.println(lista.get(i).getIdcon());
			//System.out.println(lista.get(i).getNome());
			//System.out.println(lista.get(i).getFone());
			//System.out.println(lista.get(i).getEmail());
		
		request.setAttribute("contatos", lista);
		RequestDispatcher rd = request.getRequestDispatcher ("agenda.jsp");
		rd.forward(request, response);
		
		}
		
		
		
	
	protected void novoContato(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//System.out.println(request.getParameter("nome"));
		//System.out.println(request.getParameter("fone"));
		//System.out.println(request.getParameter("email"));
		

		//setar as variaveis JavaBeans
	
		contato.setNome(request.getParameter("nome"));
		contato.setFone(request.getParameter("fone"));
		contato.setEmail(request.getParameter("email"));
		
		//Invocar o método inserirContato
		dao.inserirContato(contato);
		
		//redirecionar para o documento agenda.jsp
		response.sendRedirect("main");
	}
	
	//Editar contato
	protected void listarContatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idcon = request.getParameter("idcon");
		//teste
		//System.out.println(idcon);
		contato.setIdcon(idcon);
		dao.selecionarContato(contato);
		
		//teste de recebimento
		/*System.out.println(contato.getIdcon());
		System.out.println(contato.getNome());
		System.out.println(contato.getFone());
		System.out.println(contato.getEmail());*/
		
		request.setAttribute("idcon", contato.getIdcon());
		request.setAttribute("nome", contato.getNome());
		request.setAttribute("fone", contato.getFone());
		request.setAttribute("email", contato.getEmail());
		
		//encaminhar (despachar ao documento editar.jsp)
		RequestDispatcher rd = request.getRequestDispatcher("editar.jsp");
		rd.forward(request, response);
	}
	
	
	protected void editarContatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//teste de recebimento
		/*System.out.println(request.getParameter("idcon"));
		System.out.println(request.getParameter("nome"));
		System.out.println(request.getParameter("fone"));
		System.out.println(request.getParameter("email"));*/
		
		contato.setIdcon(request.getParameter("idcon"));
		contato.setNome(request.getParameter("nome"));
		contato.setFone(request.getParameter("fone"));
		contato.setEmail(request.getParameter("email"));
		
		//executar o metodo alterarContato
		dao.alterarContato(contato);
		
		//redirecionar para o agenda.jsp
		response.sendRedirect("main");
	}
	
	protected void removerContato(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idcon = request.getParameter("idcon");
		//teste de recebimento
		//System.out.println(idcon);
		
		//setar a variável idcon no JavaBeans
		contato.setIdcon(idcon);

		dao.deletarContato(contato);
		
		response.sendRedirect("main");
	}
	
	protected void gerarRelatorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Document documento = new Document();	
		
		try {
			response.setContentType("apllication/pdf");
			response.addHeader("Content-Disposition","inline; filename=" + "contato.pdf");
			
			//Criar Documento
			PdfWriter.getInstance(documento, response.getOutputStream());
			
			//Abrir o documento
			documento.open();
			documento.add(new Paragraph("Lista de contatos"));
			documento.add(new Paragraph(" "));
			
			//Criar tabela
			PdfPTable tabela = new PdfPTable(3);
			
			//Criar cabeçalho
			PdfPCell col1 = new PdfPCell(new Paragraph("Nome"));
			PdfPCell col2 = new PdfPCell(new Paragraph("Telefone"));
			PdfPCell col3 = new PdfPCell(new Paragraph("E-mail"));
			
			tabela.addCell(col1);
			tabela.addCell(col2);
			tabela.addCell(col3);
			
			//Popular a tabela com os contatos
			ArrayList<JavaBeans> lista = dao.listarContatos();
			
			for(int i = 0; i < lista.size(); i++) {
				tabela.addCell(lista.get(i).getNome());
				tabela.addCell(lista.get(i).getFone());
				tabela.addCell(lista.get(i).getEmail());
			}
			
			documento.add(tabela);
			
			documento.close();
			
		} catch (Exception e) {
			System.out.println(e);
			documento.close();
		}
	}
	
	
}


