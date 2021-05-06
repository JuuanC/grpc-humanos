package demo.humanos.service;

import io.grpc.stub.StreamObserver;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import ssaver.gob.mx.humanos.catalogos.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UsuarioService extends UsuarioMethodGrpc.UsuarioMethodImplBase {

    @Inject
    PgPool client;

    @Override
    public void getUsuariosGRPC(EmptyGRPC request, StreamObserver<listUsuariosGRPC> responseObserver) {
        client.query("SELECT * FROM catalogo.usuario").execute(event -> {
            if (event.succeeded()) {
                RowSet<Row> rows = event.result();

                listUsuariosGRPC.Builder getUsuarios = ssaver.gob.mx.humanos.catalogos.listUsuariosGRPC.newBuilder();

                rows.forEach(row -> {
                    UsuarioGRPC usuarioGRPC = UsuarioGRPC.newBuilder()
                            .setIdUsuario(row.getInteger("id_usuario"))
                            .setNombre(row.getString("nombre"))
                            .setApellidoPaterno(row.getString("apellido_paterno"))
                            .setApellidoMaterno(row.getString("apellido_materno"))
                            .setNombreUsuario(row.getString("nombre_usuario"))
                            .build();

                    getUsuarios.addUsuarios(usuarioGRPC);
                });
                responseObserver.onNext(getUsuarios.build());
                responseObserver.onCompleted();
            }
        });
    }

    @Override
    public void getUsuarioByIdGRPC(idUsuarioGRPC request, StreamObserver<UsuarioGRPC> responseObserver) {
        client.preparedQuery("SELECT * FROM catalogo.usuario WHERE id_usuario = $1")
                .execute(Tuple.of(request.getId()), event -> {
                    if (event.succeeded()) {
                        RowSet<Row> rows = event.result();
                        UsuarioGRPC.Builder usuarioGRPC = UsuarioGRPC.newBuilder();

                        rows.forEach(row -> {
                            usuarioGRPC
                                    .setIdUsuario(row.getInteger("id_usuario"))
                                    .setNombre(row.getString("nombre"))
                                    .setApellidoPaterno(row.getString("apellido_paterno"))
                                    .setApellidoMaterno(row.getString("apellido_materno"))
                                    .setNombreUsuario(row.getString("nombre_usuario"))
                                    .build();
                        });

                        responseObserver.onNext(usuarioGRPC.build());
                        responseObserver.onCompleted();
                    }
                });
    }

    @Override
    public void getUsuarioByUsuario(usuario request, StreamObserver<UsuarioGRPC> responseObserver) {
        client.preparedQuery("SELECT * FROM catalogo.usuario WHERE nombre_usuario = $1")
                .execute(Tuple.of(request.getNombreUsuario()), event -> {
                    if (event.succeeded()) {
                        RowSet<Row> rows = event.result();
                        UsuarioGRPC.Builder usuarioGRPC = UsuarioGRPC.newBuilder();

                        rows.forEach(row -> {
                            usuarioGRPC
                                    .setIdUsuario(row.getInteger("id_usuario"))
                                    .setNombre(row.getString("nombre"))
                                    .setApellidoPaterno(row.getString("apellido_paterno"))
                                    .setApellidoMaterno(row.getString("apellido_materno"))
                                    .setNombreUsuario(row.getString("nombre_usuario"))
                                    .build();
                        });

                        responseObserver.onNext(usuarioGRPC.build());
                        responseObserver.onCompleted();
                    }
                });
    }
}