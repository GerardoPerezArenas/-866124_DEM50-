<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="ISO-8859-1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pruebas MELANBIDE11 - Índice</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.3);
        }
        h1 {
            color: #2b62b5;
            text-align: center;
            margin-bottom: 10px;
        }
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 30px;
        }
        .test-card {
            border: 2px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin: 15px 0;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        .test-card:hover {
            border-color: #2b62b5;
            box-shadow: 0 5px 15px rgba(43,98,181,0.2);
            transform: translateY(-2px);
        }
        .test-card h2 {
            margin: 0 0 10px 0;
            color: #2b62b5;
            font-size: 20px;
        }
        .test-card p {
            margin: 5px 0;
            color: #666;
            line-height: 1.6;
        }
        .test-card .icon {
            font-size: 40px;
            float: left;
            margin-right: 15px;
        }
        .test-card .content {
            overflow: hidden;
        }
        .button {
            display: inline-block;
            background: #2b62b5;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 10px;
            transition: background 0.3s ease;
        }
        .button:hover {
            background: #1e4a8c;
        }
        .info-box {
            background: #e7f3ff;
            border-left: 4px solid #2196F3;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
        }
        .footer {
            text-align: center;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
            color: #999;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🧪 Pruebas MELANBIDE11</h1>
        <p class="subtitle">Sistema de Pruebas Automatizadas</p>
        
        <div class="info-box">
            <strong>ℹ️ Información:</strong> Este módulo proporciona acceso a las diferentes herramientas 
            de prueba para el sistema MELANBIDE11 (gestión de contratos y subsidios de empleo).
        </div>
        
        <div class="test-card" onclick="window.location.href='test_crud_automatizado.html'">
            <div class="icon">🔬</div>
            <div class="content">
                <h2>Pruebas CRUD Automatizadas</h2>
                <p>Batería completa de pruebas automatizadas que incluye:</p>
                <ul>
                    <li>Pruebas CRUD de Contrataciones</li>
                    <li>Validaciones de formularios</li>
                    <li>Pruebas de Desglose RSB (Tabs 1 y 2)</li>
                    <li>Pruebas de Modal y UI</li>
                    <li>Verificación de eliminación de scrolls horizontales</li>
                </ul>
                <a href="test_crud_automatizado.html" class="button">Acceder a Pruebas CRUD →</a>
            </div>
        </div>
        
        <div class="test-card" onclick="window.location.href='melanbide11.jsp'">
            <div class="icon">📋</div>
            <div class="content">
                <h2>Módulo Principal MELANBIDE11</h2>
                <p>Acceso directo al módulo principal de gestión de contrataciones y subsidios.</p>
                <p>Incluye funcionalidades completas de:</p>
                <ul>
                    <li>Gestión de contrataciones</li>
                    <li>Desglose salarial (RSB)</li>
                    <li>Gestión de ayudas de minimis</li>
                </ul>
                <a href="melanbide11.jsp" class="button">Acceder al Módulo →</a>
            </div>
        </div>
        
        <div class="test-card" onclick="window.location.href='nuevaContratacion.jsp'">
            <div class="icon">➕</div>
            <div class="content">
                <h2>Nueva Contratación</h2>
                <p>Formulario para dar de alta una nueva contratación.</p>
                <p>Permite registrar todos los datos del contrato y del trabajador.</p>
                <a href="nuevaContratacion.jsp" class="button">Crear Nueva Contratación ✏️</a>
            </div>
        </div>
        
        <div class="test-card" onclick="window.location.href='desglose/m11Desglose.jsp'">
            <div class="icon">💰</div>
            <div class="content">
                <h2>Desglose RSB</h2>
                <p>Desglose de Retribuciones Salariales y Beneficios.</p>
                <p>Incluye:</p>
                <ul>
                    <li>Tab 1: Salario base y pagos extraordinarios</li>
                    <li>Tab 2: Complementos salariales y otras percepciones</li>
                </ul>
                <a href="desglose/m11Desglose.jsp" class="button">Acceder a Desglose →</a>
            </div>
        </div>
        
        <div class="test-card" onclick="window.location.href='minimis.jsp'">
            <div class="icon">🇪🇺</div>
            <div class="content">
                <h2>Ayudas de Minimis</h2>
                <p>Gestión de ayudas de minimis de la UE.</p>
                <p>Control de subsidios estatales según normativa europea.</p>
                <a href="minimis.jsp" class="button">Acceder a Minimis →</a>
            </div>
        </div>
        
        <div class="footer">
            <p>MELANBIDE11 - Módulo de Gestión de Contratos DEM50</p>
            <p>Sistema Flexia © Framework Altia</p>
        </div>
    </div>
</body>
</html>
