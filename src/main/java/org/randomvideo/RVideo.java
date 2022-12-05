package org.randomvideo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RVideo {
  @Builder.Default
  private Integer index = -1;
  @Builder.Default
  private String absolutePath = "";
}
