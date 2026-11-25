-- =====================================================
-- INSERT: USER ADMIN
-- Usuário admin gerado para produção
-- =====================================================

INSERT INTO users (name, email, password, phone, role)
VALUES (
        'Beatriz Jacob',
        '${ADMIN_EMAIL}',
        '${ADMIN_PASS}',
        '11977229823',
        'ADMIN'
       );