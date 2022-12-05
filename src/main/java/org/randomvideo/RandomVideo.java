package org.randomvideo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

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

  // TODO: 04/12/2022 inserir doc
  protected RVideo sortearRVideo(List<RVideo> rVideos) {
    RVideo rVideo;
    if (!rVideos.isEmpty()) {
      final Random random = new Random();
      final int randomInt = random.ints(0, (rVideos.size() - 1)).findFirst().getAsInt();
      rVideo = rVideos.stream().filter(rvideo -> rvideo.getIndex() == randomInt).findFirst().get();
    } else {
      rVideo = RVideo.builder().build();
    }
    return rVideo;
  }

  // TODO: 04/12/2022 inserir doc
  protected void executarVideo(String videoPath) {
    final ProcessBuilder processBuilder = new ProcessBuilder();
    final String command = videoPath;
    processBuilder.command("xdg-open", command);

    try {
      final Process start = processBuilder.start();
      final StringBuilder stringBuilder = new StringBuilder();
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream()));

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line + "\n");
      }

      int exitVal = start.waitFor();
      if (exitVal == 0) {
        System.out.println("Success!");
        System.out.println(stringBuilder);

      } else {
        System.out.println("FUCK!");
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      System.exit(0);
    }
  }

  // TODO: 03/12/2022 inserir doc
  public void sortearVideo() {
    final String pathName = Sistema.DEFAULT_PATH;
    File directory = new File(pathName);

    List<File> files = listVideoFiles(directory);

    if (files != null && !files.isEmpty()) {
      final List<RVideo> rVideos = gerarListaRVideo(files);
      final RVideo rVideo = sortearRVideo(rVideos);
      executarVideo(rVideo.getAbsolutePath());
    }
  }
}