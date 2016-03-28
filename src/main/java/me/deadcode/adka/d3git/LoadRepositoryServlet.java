package me.deadcode.adka.d3git;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/loadRepositoryServlet")
public class LoadRepositoryServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String repositoryPath = request.getParameter("repositoryPath");
        String location = request.getParameter("location");

        //TODO load the data to elastic here

        Map<LocalDate, Long> data;
        if (location.equals("local")) {
            data = Main.loadLocalRepo(repositoryPath); //TODO format? \ -> \\, later filePicker
        } else {
            data = Main.loadGithubRepo(repositoryPath);
        }

        request.setAttribute("data", Main.mapToJSON(data));

        request.getRequestDispatcher("/me/deadcode/adka/d3git/visualize.jsp").forward(request, response);
    }

}
