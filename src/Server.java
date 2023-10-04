import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server{
    private static final Map<String, String> typeOutput = new HashMap<>();

    static {
        typeOutput.put(".html", "text/html");
        typeOutput.put(".css", "text/css");
        typeOutput.put(".png", "image/png");
        typeOutput.put(".jpg", "image/jpeg");
        typeOutput.put(".jpeg", "image/jpeg");
        typeOutput.put(".gif", "image/gif");
    }

    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor HTTP iniciado na porta" + port + ": \n" 
            + "Digite http://localhost:" + port + " no seu navegador para acessar o servidor.\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(new ClientThread(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientThread implements Runnable {
        private Socket clientSocket;
        private static final String basePath = "www";

        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String requestLine = reader.readLine();
                String[] requestParts = requestLine.split(" ");

                if (requestParts.length == 3 && requestParts[0].equals("GET")) {
                    String requestedFile = requestParts[1];
                    if (requestedFile.equals("/") || requestedFile.equals("/index.html")) {
                        requestedFile = "/site.html";
                    }

                    String filePath = basePath + requestedFile;

                    File file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        String fileExtension = getFileExtension(filePath);
                        String typeOutputFile = typeOutput.getOrDefault(fileExtension, "application/octet-stream");

                        String response = "HTTP/1.1 200 OK\r\n";
                        response += "Content-Type: " + typeOutputFile + "\r\n";
                        response += "Content-Length: " + file.length() + "\r\n\r\n";
                        outputStream.write(response.getBytes());

                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        fileInputStream.close();
                    } else {
                        String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                        outputStream.write(response.getBytes());
                    }
                } else {
                    String response = "HTTP/1.1 400 Bad Request\r\n\r\n";
                    outputStream.write(response.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String getFileExtension(String fileName) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex != -1) {
                return fileName.substring(dotIndex);
            }
            return "";
        }
    }
}
