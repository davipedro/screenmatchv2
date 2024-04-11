package br.com.alura.screenmatch.utils;

public interface IConverteDados {
    <T> T  obterDados(String json, Class<T> classe);
}