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
   * @return Collection de File com os arquivos filtrados.
   */
  protected Collection<File> listVideoFiles(File fileDirectory) {
    Collection<File> files = FileUtils.listFiles(
            fileDirectory,
            new RegexFileFilter("^.*(mp4|mkv|webm|flv)"),
            DirectoryFileFilter.DIRECTORY);

    return files;
  }

  // TODO: 03/12/2022 inserir doc
  public void sortearVideo() {
    final String pathName = "/home/cristofer/Downloads/";
    File directory = new File(pathName);

    Collection<File> files = listVideoFiles(directory);

    final AtomicInteger aiIndex = new AtomicInteger(0);

    final List<RVideo> rVideos = files.stream().map(file -> {
      final int andIncrement = aiIndex.getAndIncrement();
      return RVideo.builder().index(andIncrement).absolutePath(file.getAbsolutePath()).build();

    }).collect(Collectors.toList());

    final Random random = new Random();
    final int randomInt = random.ints(0, (rVideos.size() - 1)).findFirst().getAsInt();
    final RVideo rVideo = rVideos.stream().filter(rvideo -> rvideo.getIndex() == randomInt).findFirst().get();

    final ProcessBuilder processBuilder = new ProcessBuilder();
    final String command = rVideo.getAbsolutePath();
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
}