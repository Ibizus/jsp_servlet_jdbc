package org.iesvdm.jsp_servlet_jdbc.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.iesvdm.jsp_servlet_jdbc.dao.SocioDAO;
import org.iesvdm.jsp_servlet_jdbc.dao.SocioDAOImpl;
import org.iesvdm.jsp_servlet_jdbc.model.Socio;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//PLANTILLA DE CÓDIGO PARA SERVLETs EN INTELLIJ
//https://www.jetbrains.com/help/idea/creating-and-configuring-web-application-elements.html

//1A APROX. PATRÓN MVC -> M(dao, model y bbdd), V(jsp) & C(servlet)

//                      v--NOMBRE DEL SERVLET           v--RUTAS QUE ATIENDE, PUEDE SER UN ARRAY {"/GrabarSociosServlet", "/grabar-socio"}
@WebServlet(name = "BorrarSociosServlet", value = "/BorrarSociosServlet")
public class BorrarSociosServlet extends HttpServlet {

    //EL SERVLET TIENE INSTANCIADO EL DAO PARA ACCESO A BBDD A LA TABLA SOCIO
    //                                  |
    //                                  V
    private SocioDAO socioDAO = new SocioDAOImpl();

    //HTML5 SÓLO SOPORTA GET Y POST
    //FRENTE A API REST UTLIZANDO CÓDIGO DE CLIENTE JS HTTP: GET, POST, PUT, DELETE, PATCH


    //MÉTODO PARA RUTAS POST /BorrarSociosServlet
    //PARA LA RUTA POST /BorrarSociosServlet HAY 2 OPCIONES DE REDIRECCIÓN INTERNA A JSP
    //1a CASO DE QUE SE VALIDE CORRECTAMENTE --> pideNumeroSocio.jsp
    //2o CASO DE QUE NO SE VALIDE CORRECTAMENTE --> pideNumeroSocio.jsp CON INFORME DE ERROR
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestDispatcher dispatcher = null;

        //CÓDIGO DE VALIDACIÓN ENCAPSULADO EN UN MÉTODO DE UTILERÍA
        // SI OK ==> OPTIONAL CON SOCIO                 |
        // SI FAIL ==> EMPTY OPTIONAL                   |
        //
        //
        //                                              V
        int numeroSocio = Integer.parseInt(request.getParameter("codigo"));

        Optional<Socio> optionalSocio = this.socioDAO.find(numeroSocio);

        //SI OPTIONAL CON SOCIO PRESENTE <--> VALIDA OK
        if (optionalSocio.isPresent()) {

            //ACCEDO AL VALOR DE OPTIONAL DE SOCIO
            Socio socio = optionalSocio.get();
            // Borro el socio:
            this.socioDAO.delete(socio.getSocioId());


            // Antes de redireccionar tengo que setear el nuevo listado de socios
            // para que el jsp pueda volver a cargar el listado de socios:
            List<Socio> listado = this.socioDAO.getAll();
            request.setAttribute("listado", listado);

            request.setAttribute("IDSocioBorrado", numeroSocio);

            //POR ÚLTIMO, REDIRECCIÓN INTERNA PARA LA URL /GrabarSocioServlet A pideNumeroSocio.jsp
            //                                                                      |
            //                                                                      V
            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/listadoSociosB.jsp");
        } else {

            //El OPTIONAL ESTÁ VACÍO (EMPTY)
            //PREPARO MENSAJE DE ERROR EN EL ÁMBITO DEL REQUEST PARA LA VISTA JSP
            //                                |
            //                                V
            request.setAttribute("error", "Error de validación!");

            //POR ÚLTIMO, REDIRECCIÓN INTERNA PARA LA URL /GrabarSocioServlet A formularioSocio.jsp
            //                                                                      |
            //                                                                      V
            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/listadoSociosB.jsp");
        }


        //SIEMPRE PARA HACER EFECTIVA UNA REDIRECCIÓN INTERNA DEL SERVIDOR
        //TENEMOS QUE HACER FORWARD CON LOS OBJETOS request Y response
        dispatcher.forward(request,response);

    }

}