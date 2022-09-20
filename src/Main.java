
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.Scanner;
import java.io.*;
import java.lang.*;
import java.util.*;


public class Main {
    //DECLARAÇÃO DA CLASSE----------------------------
    public static class Aeroporto{
        int id;
        String initials;
        String name;
        String city;
        String state;
        String latitude;
        String longitude;
    }
    //OBTER DADOS DO BANCO DE DADOS NO MYSQL------------------------------
    public static ArrayList<String> listas(String request) {
        ArrayList<String> dados = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/aeroportos", "root", "senha");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from planilhaaeroportos");
            while (resultSet.next()) {
                dados.add(resultSet.getString(request));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dados;
    }

    //------------------------FUNÇOES PARA APLICAR DIJKSTRA------------------
    private static final int NO_PARENT = -1;
    private static void dijkstra(Double[][] adjacencyMatrix,
                                 int startVertex, int vertexFinal, ArrayList<Aeroporto> aeroportos)
    {
        int nVertices = adjacencyMatrix[0].length;

        Double[] shortestDistances = new Double[nVertices];

        boolean[] added = new boolean[nVertices];

        for (int vertexIndex = 0; vertexIndex < nVertices;
             vertexIndex++)
        {
            shortestDistances[vertexIndex] = Double.MAX_VALUE;
            added[vertexIndex] = false;
        }

        shortestDistances[startVertex] = 0.0;


        int[] parents = new int[nVertices];


        parents[startVertex] = NO_PARENT;

        for (int i = 1; i < nVertices; i++)
        {


            int nearestVertex = -1;
            Double shortestDistance = Double.MAX_VALUE;
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++)
            {
                if (!added[vertexIndex] &&
                        shortestDistances[vertexIndex] <
                                shortestDistance)
                {
                    nearestVertex = vertexIndex;
                    shortestDistance = shortestDistances[vertexIndex];
                }
            }

            added[nearestVertex] = true;


            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++)
            {
                Double edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];

                if (edgeDistance > 0
                        && ((shortestDistance + edgeDistance) <
                        shortestDistances[vertexIndex]))
                {
                    parents[vertexIndex] = nearestVertex;
                    shortestDistances[vertexIndex] = shortestDistance +
                            edgeDistance;
                }
            }
        }

        printSolution(startVertex, shortestDistances, parents, vertexFinal, aeroportos);
    }

    private static void printSolution(int startVertex,
                                      Double[] distances,
                                      int[] parents, int vertexFinal, ArrayList<Aeroporto> aeroportos )
    {
        int nVertices = distances.length;
        System.out.print("Aeroportos\t Distancia(metros)\t\t\tCaminho");
        System.out.print("\n" + aeroportos.get(startVertex).initials + " -> ");
        System.out.print(aeroportos.get(vertexFinal).initials + " \t\t ");
        System.out.print(distances[vertexFinal] + "\t\t");
        printPath(vertexFinal, parents, aeroportos);

    }

    private static void printPath(int currentVertex,
                                  int[] parents, ArrayList<Aeroporto> aeroportos)
    {

        if (currentVertex == NO_PARENT)
        {
            return;
        }
        printPath(parents[currentVertex], parents, aeroportos);
        System.out.print(aeroportos.get(currentVertex).initials + "->");
    }

    //--------------------CALCULO DA DISTANCIA---------------------------------
    public static double distancia(double lat1, double lon1, double lat2,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;
        return distance;
    }
    //----------------FUNÇÃO MAIN--------------------
    public static void main(String[] args){
        //PARTE 1 ------ CRIAÇÃO DOS OBJETOS AEROPORTOS-----------
        ArrayList<Aeroporto> aeroportos = new ArrayList<>();
        ArrayList<String> inicias = listas("initials");
        ArrayList<String> nomes = listas("name");
        ArrayList<String> cidades = listas("city");
        ArrayList<String> estados = listas("state");
        ArrayList<String> lat = listas("latitude");
        ArrayList<String> lon = listas("longitude");

        for(int i=0;i<40;i++) {
            Aeroporto aux = new Aeroporto();
            aux.id=i;
            aux.initials = inicias.get(i);
            aux.name = nomes.get(i);
            aux.city = cidades.get(i);
            aux.state = estados.get(i);
            aux.latitude = lat.get(i);
            aux.longitude = lon.get(i);
            aeroportos.add(aux);
        }
        //PARTE 2 ----------------LISTAR AEROPORTOS-----------------
        System.out.println("Lista de Aeroportos: ");
        System.out.println("SIGLA \t NOME \t CIDADE \t ESTADO \t ID");
        for(int i=0;i<40;i++){
            System.out.println(aeroportos.get(i).initials+"////"+aeroportos.get(i).name+"////"+aeroportos.get(i).city+"////"+aeroportos.get(i).state+"////id:"+aeroportos.get(i).id);
        }
        //PARTE 3 ----------------ESCOLHA DOS AEROPORTOS----------------
        System.out.println("Escolha pela sigla do aeroporto de partida: ");
        Scanner input = new Scanner(System.in);
        String sigla1 = input.next();
        System.out.println("Escolha pela sigla do aeroporto de destino: ");
        String sigla2 = input.next();
        Aeroporto escolhido1=new Aeroporto();
        Aeroporto escolhido2=new Aeroporto();
        for(int i=0;i<40;i++){
            if(Objects.equals(sigla1, aeroportos.get(i).initials)){
                escolhido1 = aeroportos.get(i);
            }
            if(Objects.equals(sigla2, aeroportos.get(i).initials)){
                escolhido2 = aeroportos.get(i);
            }
        }

        //PARTE 4 ---------------CRIAÇÃO DA MATRIZ ADJACENCIA-----------
        Double[][] matrizDistancias = new Double[40][40];
        for(int i=0;i<40;i++){
            for(int j=0;j<40;j++){
                matrizDistancias[i][j]= distancia(Double.parseDouble(aeroportos.get(i).latitude), Double.parseDouble(aeroportos.get(i).longitude), Double.parseDouble(aeroportos.get(j).latitude), Double.parseDouble(aeroportos.get(j).longitude));
            }
        }
        //MUDANDO A MATRIZ DE ADJ RESTRINGINDO PARA QUE NÃO SEJA POSSIVEL IR PELO CAMINHO DIRETO
        matrizDistancias[escolhido1.id][escolhido2.id]=Double.POSITIVE_INFINITY;
        matrizDistancias[escolhido2.id][escolhido1.id]=Double.POSITIVE_INFINITY;
        //PARTE 5 --------------------APLICANDO O ALGORITMO DE DIJSKTRA-----------------
        dijkstra(matrizDistancias, escolhido1.id, escolhido2.id, aeroportos);

    }


}