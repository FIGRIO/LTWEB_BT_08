package kt.bt08.graphql_demo.dto;

public record ProductInput(
    String title,
    Double price,
    Integer quantity,
    String descr,
    Long userid,
    Long categoryId
) {}