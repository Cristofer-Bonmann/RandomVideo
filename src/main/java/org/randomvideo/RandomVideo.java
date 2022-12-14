package org.randomvideo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RandomVideo {

  /**
   * Filtra arquivos de um diretório(incluíndo os sub-diretórios) que tenham as extensões: mp4, mkv, webm e flv.
   * @param fileDirectory diretório de onde os arquivos serão filtrados.
   * @return List de File com os arquivos filtrados.
   */
  protected List<File> listVideoFiles(File fileDirectory) {
    Collection<File> files = FileUtils.listFiles(
            fileDirectory,
            new RegexFileFilter("^.*(mp4|mkv|webm|flv)"),
            DirectoryFileFilter.DIRECTORY);

    return files.stream().collect(Collectors.toList());
  }

  /**
   * Gera lista de objetos 'RVideo' com os objetos 'File' da lista passada por parâmetro(files).
   * @param files lista de 'File'.
   * @return lista de objetos 'RVideo'.
   */
  protected List<RVideo> gerarListaRVideo(List<File> files) {
    final AtomicInteger aiIndex = new AtomicInteger(0);

    final List<RVideo> rVideos = files.stream().map(file -> {
      final int andIncrement = aiIndex.getAndIncrement();

      return RVideo.builder()
              .index(andIncrement)
              .absolutePath(file.getAbsolutePath())
              .build();

    }).collect(Collectors.toList());

    return rVideos;
  }

  /**
   * Sorteia um item da lista de objetos 'RVideo'.
   * @param rVideos lista de RVideo.
   * @return retorna objeto sorteado.
   */
  protected RVideo sortearRVideo(List<RVideo> rVideos) {
    RVideo rVideo;
    if (rVideos != null && !rVideos.isEmpty()) {
      final Random random = new Random();
      final int randomInt = random.ints(0, (rVideos.size() - 1)).findFirst().getAsInt();
      rVideo = rVideos.stream().filter(rvideo -> rvideo.getIndex() == randomInt).findFirst().get();
    } else {
      rVideo = RVideo.builder().build();
    }
    return rVideo;
  }

  /**
   * Executa o vídeo o arquivo de vídeo(videoPath).
   * @param videoPath caminho do arquivo que será executado.
   * @throws IOException
   * @throws InterruptedException
   */
  protected void executarVideo(String videoPath) throws IOException, InterruptedException {
    final ProcessBuilder processBuilder = new ProcessBuilder();
    final String command = videoPath;
    processBuilder.command("xdg-open", command);

    final Process start = processBuilder.start();
    final StringBuilder stringBuilder = new StringBuilder();
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream()));

    String line;
    while ((line = bufferedReader.readLine()) != null) {
      stringBuilder.append(line + "\n");
    }

    int exitVal = start.waitFor();
    if (exitVal == 0) {
      System.out.println(">>> Executando: " + videoPath);
      System.out.println(stringBuilder);

    } else {
      System.out.println("Alguma coisa deu errado!");
    }
  }

  /**
   * Exibe notificação de falha na execução do arquivo.
   */
  protected void notificarFalha(String videoAbsolutPath, String exceptionMessage) {
    final String msg = String.format("Não foi possível executar o arquivo: %s - %s", videoAbsolutPath, exceptionMessage);
    System.err.println(msg);
  }

  /**
   * Exibe notificação no console.
   * @param msg mensagem que será exibida.
   */
  protected void notificar(String msg) {
    System.err.println(msg);
  }

  /**
   * Sorteia um arquivo no diretório específicado, executa o arquivo sorteado e executa esse arquivo. <br>
   * Caso seja disparada alguma exceção na execução do arquivo, uma mensagem será exeibida no console.
   */
  public void sortearVideo(String[] args) {

    String folder;
    if (args != null && args.length == 1) {
      folder = args[0];
    } else {
      folder = Sistema.DEFAULT_PATH;
    }

    File directory = new File(folder);

    List<File> files = listVideoFiles(directory);

    if (files != null && !files.isEmpty()) {
      final List<RVideo> rVideos = gerarListaRVideo(files);
      final RVideo rVideo = sortearRVideo(rVideos);

      try {
        executarVideo(rVideo.getAbsolutePath());
      } catch(IOException | InterruptedException e) {
        notificarFalha(rVideo.getAbsolutePath(), e.getMessage());
      }

    } else {
      final String msg = String.format("Nenhum arquivo de vídeo foi encontrado em %s", directory.getAbsolutePath());
      notificar(msg);
    }
  }
}