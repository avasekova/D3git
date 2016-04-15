package me.deadcode.adka.d3git;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/loadRepositoryServlet")
public class LoadRepositoryServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String repositoryPath = request.getParameter("repositoryPath");
        String location = request.getParameter("location");

        if (location.equals("local")) {
            ElasticsearchLoader.loadLocalRepo(repositoryPath); //TODO format? \ -> \\, later filePicker
        } else {
            ElasticsearchLoader.loadGithubRepo(repositoryPath);
        }

        request.getRequestDispatcher("/me/deadcode/adka/d3git/visualize.jsp").forward(request, response);
    }

}
